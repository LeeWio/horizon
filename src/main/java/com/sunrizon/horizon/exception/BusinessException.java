package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Custom business exception for standardized error handling.
 * <p>
 * Best Practice: Throw this exception in service layer instead of returning error responses.
 * GlobalExceptionHandler will automatically convert it to unified error response.
 * <p>
 * Usage:
 * <pre>
 * // Simple usage
 * throw new BusinessException(ResponseCode.USER_NOT_FOUND);
 * 
 * // With custom message
 * throw new BusinessException(ResponseCode.USER_NOT_FOUND, "User with ID " + userId + " not found");
 * 
 * // With cause
 * throw new BusinessException(ResponseCode.DATABASE_ERROR, e);
 * </pre>
 */
@Getter
public class BusinessException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** Standard response code enum */
  private final ResponseCode responseCode;
  
  /** Custom error message (optional, overrides ResponseCode.message) */
  private final String customMessage;

  /**
   * Create exception with standard ResponseCode.
   * Uses the default message from ResponseCode.
   */
  public BusinessException(ResponseCode responseCode) {
    super(responseCode.getMessage());
    this.responseCode = responseCode;
    this.customMessage = null;
  }

  /**
   * Create exception with custom message.
   * The custom message will override ResponseCode's default message.
   */
  public BusinessException(ResponseCode responseCode, String customMessage) {
    super(customMessage);
    this.responseCode = responseCode;
    this.customMessage = customMessage;
  }

  /**
   * Create exception with standard ResponseCode and cause.
   */
  public BusinessException(ResponseCode responseCode, Throwable cause) {
    super(responseCode.getMessage(), cause);
    this.responseCode = responseCode;
    this.customMessage = null;
  }
  
  /**
   * Create exception with custom message and cause.
   */
  public BusinessException(ResponseCode responseCode, String customMessage, Throwable cause) {
    super(customMessage, cause);
    this.responseCode = responseCode;
    this.customMessage = customMessage;
  }
  
  /**
   * Get the final error message (custom or default).
   */
  public String getErrorMessage() {
    return customMessage != null ? customMessage : responseCode.getMessage();
  }
}
