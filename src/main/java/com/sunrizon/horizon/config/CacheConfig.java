package com.sunrizon.horizon.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Multi-level cache configuration
 * <p>
 * Implements two-level caching strategy:
 * - L1: Local Caffeine cache (fast, in-memory, limited capacity)
 * - L2: Distributed Redis cache (shared across instances, larger capacity)
 * <p>
 * Cache names and TTL:
 * - articles: Hot article list (5 minutes)
 * - article: Single article details (10 minutes)
 * - user: User information (15 minutes)
 * - category: Categories (30 minutes)
 * - tag: Tags (30 minutes)
 * - hotArticles: Hot article rankings (5 minutes)
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

  /**
   * Local cache manager (Caffeine) - Primary cache
   * <p>
   * Advantages:
   * - Ultra-fast access (nanosecond level)
   * - No network overhead
   * - Suitable for hot data
   */
  @Bean
  @Primary
  public CacheManager caffeineCacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(Caffeine.newBuilder()
        // Maximum 1000 entries per cache
        .maximumSize(1000)
        // Expire after write (5 minutes default)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        // Expire after access (10 minutes)
        .expireAfterAccess(10, TimeUnit.MINUTES)
        // Enable statistics
        .recordStats()
        // Initial capacity
        .initialCapacity(100));

    // Configure specific cache names
    cacheManager.setCacheNames(java.util.Arrays.asList(
        "articles", "article", "user", "category", "tag", "hotArticles"
    ));

    log.info("Caffeine local cache manager initialized with {} caches",
        cacheManager.getCacheNames().size());

    return cacheManager;
  }

  /**
   * Distributed cache manager (Redis) - Secondary cache
   * <p>
   * Advantages:
   * - Shared across multiple instances
   * - Larger capacity
   * - Persistence support
   */
  @Bean
  public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    // Default cache configuration
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        // Default TTL: 10 minutes
        .entryTtl(Duration.ofMinutes(10))
        // Cache null values to prevent cache penetration
        .disableCachingNullValues()
        // Key serializer: String
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        // Value serializer: JSON
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));

    // Custom TTL for specific caches
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

    // Hot articles: 5 minutes
    cacheConfigurations.put("articles",
        defaultConfig.entryTtl(Duration.ofMinutes(5)));

    // Single article: 10 minutes
    cacheConfigurations.put("article",
        defaultConfig.entryTtl(Duration.ofMinutes(10)));

    // User info: 15 minutes
    cacheConfigurations.put("user",
        defaultConfig.entryTtl(Duration.ofMinutes(15)));

    // Categories: 30 minutes (rarely change)
    cacheConfigurations.put("category",
        defaultConfig.entryTtl(Duration.ofMinutes(30)));

    // Tags: 30 minutes
    cacheConfigurations.put("tag",
        defaultConfig.entryTtl(Duration.ofMinutes(30)));

    // Hot article rankings: 5 minutes
    cacheConfigurations.put("hotArticles",
        defaultConfig.entryTtl(Duration.ofMinutes(5)));

    RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultConfig)
        .withInitialCacheConfigurations(cacheConfigurations)
        .transactionAware()
        .build();

    log.info("Redis cache manager initialized with {} custom cache configurations",
        cacheConfigurations.size());

    return cacheManager;
  }
}
