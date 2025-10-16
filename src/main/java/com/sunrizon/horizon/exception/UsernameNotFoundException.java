package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;

/**
 * Exception thrown when a user is not found by username or email.
 * <p>
 * This is a specialized ResourceNotFoundException for user-related queries.
 * <p>
 * Usage:
 * <pre>
 * throw new UsernameNotFoundException("user123");
 * throw new UsernameNotFoundException("user@example.com");
 * </pre>
 */
public class UsernameNotFoundException extends ResourceNotFoundException {

  private static final long serialVersionUID = 1L;

  /**
   * Create exception with username/email in error message.
   */
  public UsernameNotFoundException(String usernameOrEmail) {
    super(ResponseCode.USER_NOT_FOUND, 
          String.format("User not found: %s", usernameOrEmail));
  }

  /**
   * Create exception with default message.
   */
  public UsernameNotFoundException() {
    super(ResponseCode.USER_NOT_FOUND);
  }

  /**
   * Create exception with custom message.
   */
  public UsernameNotFoundException(String message, boolean useCustomMessage) {
    super(ResponseCode.USER_NOT_FOUND, message);
  }

  /**
   * Create exception with cause.
   */
  public UsernameNotFoundException(Throwable cause) {
    super(ResponseCode.USER_NOT_FOUND, cause);
  }
}
