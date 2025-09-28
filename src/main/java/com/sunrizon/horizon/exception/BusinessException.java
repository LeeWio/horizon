package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Custom business exception for standardized error handling.
 */
@Getter
@Setter
@ToString
public class BusinessException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** Standard response code enum */
  private final ResponseCode responseCode;

  /**
   * Create exception with standard ResponseCode.
   */
  public BusinessException(ResponseCode responseCode) {
    super(responseCode.getMessage());
    this.responseCode = responseCode;
  }

  public BusinessException(ResponseCode responseCode, String message) {
    super(message);
    this.responseCode = responseCode;
  }

  /**
   * Create exception with standard ResponseCode and cause.
   */
  public BusinessException(ResponseCode responseCode, Throwable cause) {
    super(responseCode.getMessage(), cause);
    this.responseCode = responseCode;
  }

}
