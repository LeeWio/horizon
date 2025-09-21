package com.sunrizon.horizon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * 请求频率限制配置
 * 基于 Redis 实现分布式限流
 */
@Configuration
public class RateLimitConfig {

    @Value("${rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;

    @Value("${rate-limit.burst-capacity:200}")
    private int burstCapacity;

    /**
     * 配置限流器
     */
    @Bean
    public RateLimiter rateLimiter(StringRedisTemplate redisTemplate) {
        return new RateLimiter(redisTemplate, requestsPerMinute, burstCapacity);
    }

    /**
     * 基于 Redis 的分布式限流器
     */
    public static class RateLimiter {
        private final StringRedisTemplate redisTemplate;
        private final int requestsPerMinute;
        private final int burstCapacity;

        public RateLimiter(StringRedisTemplate redisTemplate, int requestsPerMinute, int burstCapacity) {
            this.redisTemplate = redisTemplate;
            this.requestsPerMinute = requestsPerMinute;
            this.burstCapacity = burstCapacity;
        }

        /**
         * 检查是否允许请求
         * @param key 限流键（通常是用户ID或IP地址）
         * @return true 如果允许请求，false 如果被限流
         */
        public boolean isAllowed(String key) {
            String redisKey = "rate_limit:" + key;
            
            // 使用 Redis 的 INCR 和 EXPIRE 实现滑动窗口限流
            Long currentCount = redisTemplate.opsForValue().increment(redisKey);
            
            if (currentCount == 1) {
                // 第一次请求，设置过期时间
                redisTemplate.expire(redisKey, Duration.ofMinutes(1));
            }
            
            return currentCount <= requestsPerMinute;
        }

        /**
         * 获取剩余请求次数
         * @param key 限流键
         * @return 剩余请求次数
         */
        public long getRemainingRequests(String key) {
            String redisKey = "rate_limit:" + key;
            Long currentCount = redisTemplate.opsForValue().increment(redisKey);
            
            if (currentCount == 1) {
                redisTemplate.expire(redisKey, Duration.ofMinutes(1));
            }
            
            return Math.max(0, requestsPerMinute - currentCount);
        }

        /**
         * 重置限流计数器
         * @param key 限流键
         */
        public void reset(String key) {
            String redisKey = "rate_limit:" + key;
            redisTemplate.delete(redisKey);
        }
    }
}
