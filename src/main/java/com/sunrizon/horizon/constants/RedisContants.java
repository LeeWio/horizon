package com.sunrizon.horizon.constants;

/**
 * Constants for Redis keys used throughout the application.
 * Provides consistent key naming for various data stored in Redis.
 */
public class RedisContants {

  // OTP verification constants
  public static final String OTP_PREFIX = "otp:";
  public static final String OTP_KEY_FORMAT = "otp:%s"; // %s will be replaced with email
  
  // User session constants
  public static final String USER_SESSION_PREFIX = "session:";
  public static final String USER_SESSION_KEY_FORMAT = "session:%s"; // %s will be replaced with user ID
  
  // Login failure tracking constants
  public static final String LOGIN_FAILURE_PREFIX = "login_failure_count:";
  public static final String LOGIN_FAILURE_KEY_FORMAT = "login_failure_count:%s"; // %s will be replaced with IP address
  
  // Password reset token constants
  public static final String PASSWORD_RESET_TOKEN_PREFIX = "password_reset:";
  public static final String PASSWORD_RESET_TOKEN_KEY_FORMAT = "password_reset:%s"; // %s will be replaced with reset token
  
  // Blacklisted JWT token constants
  public static final String BLACKLISTED_TOKEN_PREFIX = "blacklisted_token:";
  public static final String BLACKLISTED_TOKEN_KEY_FORMAT = "blacklisted_token:%s"; // %s will be replaced with JWT token
  
  // Refresh token constants
  public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
  public static final String REFRESH_TOKEN_KEY_FORMAT = "refresh_token:%s"; // %s will be replaced with refresh token
  
  // Rate limiting constants
  public static final String RATE_LIMIT_PREFIX = "rate_limit:";
  public static final String RATE_LIMIT_KEY_FORMAT = "rate_limit:%s"; // %s will be replaced with identifier (e.g., API key, IP)
  
  // Cache constants
  public static final String USER_CACHE_PREFIX = "user:";
  public static final String USER_CACHE_KEY_FORMAT = "user:%s"; // %s will be replaced with user ID
  
  // Article cache constants
  public static final String ARTICLE_CACHE_PREFIX = "article:";
  public static final String ARTICLE_CACHE_KEY_FORMAT = "article:%s"; // %s will be replaced with article ID
  
  // Trending articles cache constants
  public static final String TRENDING_CACHE_PREFIX = "trending:";
  public static final String TRENDING_VIEWS_KEY_FORMAT = "trending:views:%s:page%d:size%d"; // timeRange, pageNumber, pageSize
  public static final String TRENDING_LIKES_KEY_FORMAT = "trending:likes:%s:page%d:size%d"; // timeRange, pageNumber, pageSize
  public static final String TRENDING_FAVORITES_KEY_FORMAT = "trending:favorites:%s:page%d:size%d"; // timeRange, pageNumber, pageSize
  public static final String TRENDING_PATTERN = "trending:*";
  
  // Cache lock constants
  public static final String CACHE_LOCK_PREFIX = "lock:";
  public static final String CACHE_LOCK_KEY_FORMAT = "lock:%s"; // %s will be replaced with cache key
  
  // Cache null value marker
  public static final String CACHE_NULL_VALUE = "NULL";
  
  // Cache TTL constants (in seconds)
  public static final long CACHE_NULL_TTL_SECONDS = 120; // 2 minutes for null values
  public static final long CACHE_ARTICLE_TTL_SECONDS = 600; // 10 minutes for articles
  public static final long CACHE_TRENDING_TTL_SECONDS = 300; // 5 minutes for trending lists
  public static final long CACHE_LOCK_TTL_SECONDS = 10; // 10 seconds for distributed lock
}
