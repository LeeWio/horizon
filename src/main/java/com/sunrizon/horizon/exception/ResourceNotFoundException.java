package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;

/**
 * Exception thrown when a requested resource is not found.
 * <p>
 * Usage examples:
 * <pre>
 * throw new ResourceNotFoundException(ResponseCode.USER_NOT_FOUND);
 * throw new ResourceNotFoundException(ResponseCode.ARTICLE_NOT_FOUND, "Article with ID " + id + " not found");
 * </pre>
 */
public class ResourceNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  public ResourceNotFoundException(ResponseCode responseCode) {
    super(responseCode);
  }

  public ResourceNotFoundException(ResponseCode responseCode, String message) {
    super(responseCode, message);
  }

  public ResourceNotFoundException(ResponseCode responseCode, Throwable cause) {
    super(responseCode, cause);
  }
}
