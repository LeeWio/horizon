package com.sunrizon.horizon.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Cache service interface
 * <p>
 * Provides unified cache operations with multi-level caching strategy
 * and protection against cache penetration, breakdown, and avalanche.
 */
public interface ICacheService {

  /**
   * Get value from cache with fallback to database
   * <p>
   * Cache strategy:
   * 1. Try local cache (Caffeine) first
   * 2. If miss, try Redis cache
   * 3. If still miss, load from database and cache the result
   *
   * @param key          Cache key
   * @param type         Value type class
   * @param dbFallback   Database fallback function (called if cache miss)
   * @param cacheName    Cache name for categorization
   * @param <T>          Value type
   * @return Cached or loaded value
   */
  <T> T get(String key, Class<T> type, Supplier<T> dbFallback, String cacheName);

  /**
   * Get value with custom TTL
   *
   * @param key          Cache key
   * @param type         Value type class
   * @param dbFallback   Database fallback function
   * @param cacheName    Cache name
   * @param ttl          Time to live
   * @param timeUnit     Time unit
   * @param <T>          Value type
   * @return Cached or loaded value
   */
  <T> T get(String key, Class<T> type, Supplier<T> dbFallback, String cacheName,
      long ttl, TimeUnit timeUnit);

  /**
   * Put value into cache
   *
   * @param key       Cache key
   * @param value     Value to cache
   * @param cacheName Cache name
   */
  void put(String key, Object value, String cacheName);

  /**
   * Put value with custom TTL
   *
   * @param key       Cache key
   * @param value     Value to cache
   * @param cacheName Cache name
   * @param ttl       Time to live
   * @param timeUnit  Time unit
   */
  void put(String key, Object value, String cacheName, long ttl, TimeUnit timeUnit);

  /**
   * Evict specific key from cache
   *
   * @param key       Cache key
   * @param cacheName Cache name
   */
  void evict(String key, String cacheName);

  /**
   * Clear all entries in a cache
   *
   * @param cacheName Cache name
   */
  void clear(String cacheName);

  /**
   * Get multiple values from cache
   *
   * @param keys      List of cache keys
   * @param type      Value type class
   * @param cacheName Cache name
   * @param <T>       Value type
   * @return List of values (null if not found)
   */
  <T> List<T> multiGet(List<String> keys, Class<T> type, String cacheName);

  /**
   * Warm up cache with hot data
   * <p>
   * Pre-loads frequently accessed data into cache to improve performance
   */
  void warmUp();

  /**
   * Get cache statistics
   *
   * @param cacheName Cache name
   * @return Cache statistics as string
   */
  String getStats(String cacheName);
}
