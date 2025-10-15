package com.sunrizon.horizon.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存服务接口
 * <p>
 * 基于Redis实现的缓存服务，提供：
 * - 缓存穿透防护（缓存空值）
 * - 缓存击穿防护（分布式锁）
 * - 缓存雪崩防护（随机过期时间）
 */
public interface ICacheService {

  /**
   * 从缓存获取数据，如果不存在则从数据库加载
   *
   * @param key        缓存键
   * @param type       返回值类型
   * @param dbFallback 数据库回调函数
   * @param ttl        过期时间
   * @param timeUnit   时间单位
   * @param <T>        返回值类型
   * @return 缓存或数据库中的数据
   */
  <T> T getWithFallback(String key, Class<T> type, Supplier<T> dbFallback, long ttl, TimeUnit timeUnit);

  /**
   * 从缓存获取数据，使用默认过期时间（10分钟）
   *
   * @param key        缓存键
   * @param type       返回值类型
   * @param dbFallback 数据库回调函数
   * @param <T>        返回值类型
   * @return 缓存或数据库中的数据
   */
  <T> T getWithFallback(String key, Class<T> type, Supplier<T> dbFallback);

  /**
   * 设置缓存
   *
   * @param key      缓存键
   * @param value    缓存值
   * @param ttl      过期时间
   * @param timeUnit 时间单位
   */
  void set(String key, Object value, long ttl, TimeUnit timeUnit);

  /**
   * 删除缓存
   *
   * @param key 缓存键
   */
  void evict(String key);

  /**
   * 删除指定前缀的所有缓存
   *
   * @param keyPattern 缓存键模式（如 "article:*"）
   */
  void evictByPattern(String keyPattern);

  /**
   * 缓存预热
   */
  void warmUp();
}
