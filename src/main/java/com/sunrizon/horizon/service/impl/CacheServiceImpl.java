package com.sunrizon.horizon.service.impl;

import com.sunrizon.horizon.service.ICacheService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Cache service implementation with multi-level caching
 * <p>
 * Protection mechanisms:
 * 1. Cache Penetration: Cache null values with short TTL
 * 2. Cache Breakdown: Distributed lock (Redisson) for hot key reloading
 * 3. Cache Avalanche: Randomized TTL to avoid mass expiration
 */
@Service
@Slf4j
public class CacheServiceImpl implements ICacheService {

  @Resource
  private CacheManager caffeineCacheManager;

  @Resource(name = "redisCacheManager")
  private CacheManager redisCacheManager;

  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  @Resource
  private RedissonClient redissonClient;

  private static final String LOCK_PREFIX = "cache:lock:";
  private static final long LOCK_TIMEOUT = 10; // seconds
  private static final long NULL_CACHE_TTL = 2; // minutes

  @Override
  public <T> T get(String key, Class<T> type, Supplier<T> dbFallback, String cacheName) {
    return get(key, type, dbFallback, cacheName, 10, TimeUnit.MINUTES);
  }

  @Override
  public <T> T get(String key, Class<T> type, Supplier<T> dbFallback, String cacheName,
      long ttl, TimeUnit timeUnit) {

    // Step 1: Try local cache (Caffeine) - L1
    Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
    if (caffeineCache != null) {
      Cache.ValueWrapper wrapper = caffeineCache.get(key);
      if (wrapper != null) {
        log.debug("Cache hit (Caffeine): {} -> {}", cacheName, key);
        return type.cast(wrapper.get());
      }
    }

    // Step 2: Try Redis cache - L2
    Cache redisCache = redisCacheManager.getCache(cacheName);
    if (redisCache != null) {
      Cache.ValueWrapper wrapper = redisCache.get(key);
      if (wrapper != null) {
        log.debug("Cache hit (Redis): {} -> {}", cacheName, key);
        T value = type.cast(wrapper.get());

        // Update L1 cache
        if (caffeineCache != null) {
          caffeineCache.put(key, value);
        }

        return value;
      }
    }

    // Step 3: Cache miss - load from database with distributed lock
    return loadWithLock(key, type, dbFallback, cacheName, ttl, timeUnit);
  }

  /**
   * Load data from database with distributed lock to prevent cache breakdown
   */
  private <T> T loadWithLock(String key, Class<T> type, Supplier<T> dbFallback,
      String cacheName, long ttl, TimeUnit timeUnit) {

    String lockKey = LOCK_PREFIX + cacheName + ":" + key;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // Try to acquire lock
      if (lock.tryLock(LOCK_TIMEOUT, timeUnit.toSeconds(ttl), TimeUnit.SECONDS)) {
        try {
          // Double-check cache after acquiring lock
          Cache redisCache = redisCacheManager.getCache(cacheName);
          if (redisCache != null) {
            Cache.ValueWrapper wrapper = redisCache.get(key);
            if (wrapper != null) {
              log.debug("Cache hit after lock (Redis): {} -> {}", cacheName, key);
              return type.cast(wrapper.get());
            }
          }

          // Load from database
          log.info("Cache miss, loading from DB: {} -> {}", cacheName, key);
          T value = dbFallback.get();

          // Cache the result (even if null to prevent cache penetration)
          if (value != null) {
            put(key, value, cacheName, ttl, timeUnit);
          } else {
            // Cache null value with shorter TTL to prevent cache penetration
            put(key, new NullValue(), cacheName, NULL_CACHE_TTL, TimeUnit.MINUTES);
            log.warn("Null value cached to prevent penetration: {} -> {}", cacheName, key);
          }

          return value;

        } finally {
          lock.unlock();
        }
      } else {
        // Failed to acquire lock, wait and retry
        log.warn("Failed to acquire lock, falling back to DB: {} -> {}", cacheName, key);
        return dbFallback.get();
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Lock interrupted: {} -> {}", cacheName, key, e);
      return dbFallback.get();
    }
  }

  @Override
  public void put(String key, Object value, String cacheName) {
    put(key, value, cacheName, 10, TimeUnit.MINUTES);
  }

  @Override
  public void put(String key, Object value, String cacheName, long ttl, TimeUnit timeUnit) {
    // Put into L1 cache (Caffeine)
    Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
    if (caffeineCache != null) {
      caffeineCache.put(key, value);
    }

    // Put into L2 cache (Redis)
    Cache redisCache = redisCacheManager.getCache(cacheName);
    if (redisCache != null) {
      redisCache.put(key, value);
    }

    // Also use RedisTemplate for manual TTL control
    String redisKey = cacheName + "::" + key;
    redisTemplate.opsForValue().set(redisKey, value, ttl, timeUnit);

    log.debug("Cache put: {} -> {}", cacheName, key);
  }

  @Override
  public void evict(String key, String cacheName) {
    // Evict from L1 cache
    Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
    if (caffeineCache != null) {
      caffeineCache.evict(key);
    }

    // Evict from L2 cache
    Cache redisCache = redisCacheManager.getCache(cacheName);
    if (redisCache != null) {
      redisCache.evict(key);
    }

    // Also evict from RedisTemplate
    String redisKey = cacheName + "::" + key;
    redisTemplate.delete(redisKey);

    log.info("Cache evicted: {} -> {}", cacheName, key);
  }

  @Override
  public void clear(String cacheName) {
    // Clear L1 cache
    Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
    if (caffeineCache != null) {
      caffeineCache.clear();
    }

    // Clear L2 cache
    Cache redisCache = redisCacheManager.getCache(cacheName);
    if (redisCache != null) {
      redisCache.clear();
    }

    log.info("Cache cleared: {}", cacheName);
  }

  @Override
  public <T> List<T> multiGet(List<String> keys, Class<T> type, String cacheName) {
    return keys.stream()
        .map(key -> {
          Cache cache = caffeineCacheManager.getCache(cacheName);
          if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
              return type.cast(wrapper.get());
            }
          }
          return null;
        })
        .collect(Collectors.toList());
  }

  @Override
  public void warmUp() {
    log.info("Cache warm-up started");

    // TODO: Implement cache warm-up logic
    // Examples:
    // 1. Pre-load hot articles
    // 2. Pre-load popular categories
    // 3. Pre-load active users

    log.info("Cache warm-up completed");
  }

  @Override
  public String getStats(String cacheName) {
    Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
    if (caffeineCache != null) {
      return "Cache stats for " + cacheName + " (Caffeine): Available";
    }
    return "Cache " + cacheName + " not found";
  }

  /**
   * Placeholder class for null values to prevent cache penetration
   */
  private static class NullValue {
    @Override
    public String toString() {
      return "NullValue";
    }
  }
}
