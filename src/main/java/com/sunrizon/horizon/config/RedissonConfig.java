package com.sunrizon.horizon.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson configuration for distributed lock and advanced Redis features
 */
@Configuration
@Slf4j
public class RedissonConfig {

  @Value("${spring.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.redis.port:6379}")
  private int redisPort;

  @Value("${spring.redis.database:0}")
  private int redisDatabase;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    
    String address = String.format("redis://%s:%d", redisHost, redisPort);
    
    config.useSingleServer()
        .setAddress(address)
        .setDatabase(redisDatabase)
        .setConnectionPoolSize(64)
        .setConnectionMinimumIdleSize(10)
        .setIdleConnectionTimeout(10000)
        .setConnectTimeout(10000)
        .setTimeout(3000)
        .setRetryAttempts(3)
        .setRetryInterval(1500);

    log.info("Redisson client initialized with address: {}, database: {}", address, redisDatabase);

    return Redisson.create(config);
  }
}
