package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;

/**
 * Exception thrown when business validation fails.
 * <p>
 * This is different from Bean Validation (@Valid) - it's for custom business logic validation.
 * <p>
 * Usage examples:
 * <pre>
 * throw new ValidationException(ResponseCode.VALIDATION_ERROR, "Username must be unique");
 * throw new ValidationException(ResponseCode.USER_PASSWORD_INCORRECT);
 * </pre>
 */
public class ValidationException extends BusinessException {

  private static final long serialVersionUID = 1L;

  public ValidationException(ResponseCode responseCode) {
    super(responseCode);
  }

  public ValidationException(ResponseCode responseCode, String message) {
    super(responseCode, message);
  }

  public ValidationException(ResponseCode responseCode, Throwable cause) {
    super(responseCode, cause);
  }
}
