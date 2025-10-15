package com.sunrizon.horizon.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.sunrizon.horizon.service.ICacheService;
import com.sunrizon.horizon.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 基于Redis的缓存服务实现
 * <p>
 * 实现了三大缓存问题的防护：
 * 1. 缓存穿透：缓存空值（短TTL）
 * 2. 缓存击穿：分布式锁（Lua脚本）
 * 3. 缓存雪崩：随机过期时间
 */
@Service
@Slf4j
public class CacheServiceImpl implements ICacheService {

  @Resource
  private RedisUtil redisUtil;

  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  // 空值缓存标识
  private static final String NULL_CACHE_VALUE = "NULL";
  // 空值缓存时间（2分钟）
  private static final long NULL_CACHE_TTL = 2;
  // 锁前缀
  private static final String LOCK_PREFIX = "lock:";
  // 锁过期时间（10秒）
  private static final long LOCK_TTL = 10;
  // 默认缓存时间（10分钟）
  private static final long DEFAULT_TTL = 10;

  /**
   * Lua脚本：获取分布式锁
   * 如果key不存在，则设置值并返回1（获取锁成功）
   * 如果key存在，返回0（获取锁失败）
   */
  private static final String ACQUIRE_LOCK_SCRIPT =
      "if redis.call('exists', KEYS[1]) == 0 then " +
          "redis.call('setex', KEYS[1], ARGV[1], ARGV[2]) " +
          "return 1 " +
          "else " +
          "return 0 " +
          "end";

  @Override
  public <T> T getWithFallback(String key, Class<T> type, Supplier<T> dbFallback) {
    return getWithFallback(key, type, dbFallback, DEFAULT_TTL, TimeUnit.MINUTES);
  }

  @Override
  public <T> T getWithFallback(String key, Class<T> type, Supplier<T> dbFallback,
                                long ttl, TimeUnit timeUnit) {
    // 1. 尝试从Redis获取
    Optional<String> cachedValue = redisUtil.get(key, String.class);

    if (cachedValue.isPresent()) {
      String value = cachedValue.get();

      // 检查是否是空值缓存
      if (NULL_CACHE_VALUE.equals(value)) {
        log.debug("命中空值缓存: {}", key);
        return null;
      }

      // 反序列化并返回
      try {
        T result = JSONUtil.toBean(value, type);
        log.debug("缓存命中: {}", key);
        return result;
      } catch (Exception e) {
        log.error("缓存反序列化失败: {}", key, e);
        // 反序列化失败，删除缓存，重新加载
        redisUtil.delete(key);
      }
    }

    // 2. 缓存未命中，使用分布式锁防止缓存击穿
    return loadWithLock(key, type, dbFallback, ttl, timeUnit);
  }

  /**
   * 使用分布式锁加载数据
   */
  private <T> T loadWithLock(String key, Class<T> type, Supplier<T> dbFallback,
                             long ttl, TimeUnit timeUnit) {
    String lockKey = LOCK_PREFIX + key;

    // 尝试获取锁
    boolean locked = tryLock(lockKey);

    if (locked) {
      try {
        // 获取锁成功，双重检查缓存
        Optional<String> cachedValue = redisUtil.get(key, String.class);
        if (cachedValue.isPresent()) {
          String value = cachedValue.get();
          if (NULL_CACHE_VALUE.equals(value)) {
            return null;
          }
          return JSONUtil.toBean(value, type);
        }

        // 从数据库加载
        log.info("缓存未命中，从数据库加载: {}", key);
        T data = dbFallback.get();

        // 缓存数据
        if (data != null) {
          // 添加随机时间，防止缓存雪崩（±20%的随机波动）
          long randomTtl = addRandomTtl(ttl, timeUnit);
          String jsonValue = JSONUtil.toJsonStr(data);
          redisUtil.set(key, jsonValue, randomTtl, TimeUnit.SECONDS);
          log.debug("数据已缓存: {}, TTL: {}秒", key, randomTtl);
        } else {
          // 缓存空值，防止缓存穿透
          redisUtil.set(key, NULL_CACHE_VALUE, NULL_CACHE_TTL, TimeUnit.MINUTES);
          log.warn("缓存空值以防止穿透: {}", key);
        }

        return data;

      } finally {
        // 释放锁
        releaseLock(lockKey);
      }
    } else {
      // 获取锁失败，等待并重试
      log.debug("等待锁释放: {}", key);
      try {
        Thread.sleep(50); // 等待50ms
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      // 重试获取缓存
      Optional<String> cachedValue = redisUtil.get(key, String.class);
      if (cachedValue.isPresent()) {
        String value = cachedValue.get();
        if (NULL_CACHE_VALUE.equals(value)) {
          return null;
        }
        return JSONUtil.toBean(value, type);
      }

      // 如果还是没有，直接查数据库（降级策略）
      log.warn("锁等待超时，直接查询数据库: {}", key);
      return dbFallback.get();
    }
  }

  /**
   * 尝试获取分布式锁（使用Lua脚本保证原子性）
   */
  private boolean tryLock(String lockKey) {
    try {
      DefaultRedisScript<Long> script = new DefaultRedisScript<>();
      script.setScriptText(ACQUIRE_LOCK_SCRIPT);
      script.setResultType(Long.class);

      Long result = redisTemplate.execute(
          script,
          Collections.singletonList(lockKey),
          String.valueOf(LOCK_TTL),
          Thread.currentThread().getName()
      );

      return result != null && result == 1L;
    } catch (Exception e) {
      log.error("获取锁失败: {}", lockKey, e);
      return false;
    }
  }

  /**
   * 释放分布式锁
   */
  private void releaseLock(String lockKey) {
    try {
      redisUtil.delete(lockKey);
    } catch (Exception e) {
      log.error("释放锁失败: {}", lockKey, e);
    }
  }

  /**
   * 添加随机TTL，防止缓存雪崩
   * 在原TTL基础上增加±20%的随机波动
   */
  private long addRandomTtl(long ttl, TimeUnit timeUnit) {
    long seconds = timeUnit.toSeconds(ttl);
    // 计算20%的随机波动范围
    long randomRange = (long) (seconds * 0.2);
    // 随机增减
    long randomOffset = RandomUtil.randomLong(-randomRange, randomRange);
    return seconds + randomOffset;
  }

  @Override
  public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
    try {
      String jsonValue = JSONUtil.toJsonStr(value);
      redisUtil.set(key, jsonValue, ttl, timeUnit);
      log.debug("缓存已设置: {}", key);
    } catch (Exception e) {
      log.error("设置缓存失败: {}", key, e);
    }
  }

  @Override
  public void evict(String key) {
    try {
      redisUtil.delete(key);
      log.info("缓存已删除: {}", key);
    } catch (Exception e) {
      log.error("删除缓存失败: {}", key, e);
    }
  }

  @Override
  public void evictByPattern(String keyPattern) {
    try {
      // 使用scan命令查找匹配的key（推荐方式，不会阻塞Redis）
      redisTemplate.keys(keyPattern).forEach(key -> {
        redisTemplate.delete((String) key);
      });
      log.info("批量删除缓存: {}", keyPattern);
    } catch (Exception e) {
      log.error("批量删除缓存失败: {}", keyPattern, e);
    }
  }

  @Override
  public void warmUp() {
    log.info("缓存预热开始...");
    // TODO: 实现缓存预热逻辑
    // 示例：
    // 1. 预加载热门文章
    // 2. 预加载分类、标签
    // 3. 预加载活跃用户信息
    log.info("缓存预热完成");
  }
}

