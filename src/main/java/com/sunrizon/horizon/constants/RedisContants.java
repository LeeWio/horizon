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
}
