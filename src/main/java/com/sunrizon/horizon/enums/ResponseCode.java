package com.sunrizon.horizon.enums;

import lombok.Getter;

/**
 * Standard API response codes.
 * <p>
 * Includes success, generic failure, and common HTTP-related codes.
 */
@Getter
public enum ResponseCode {

  /** Success */
  SUCCESS(200, "Success"),

  /** Generic failure */
  FAILURE(500, "Failure"),

  /** Resource not found */
  NOT_FOUND(404, "Not Found"),

  /** Unauthorized access */
  UNAUTHORIZED(401, "Unauthorized"),

  /** Forbidden access */
  FORBIDDEN(403, "Forbidden"),

  /** Bad request, e.g., invalid parameters */
  BAD_REQUEST(400, "Bad Request"),

  /** Conflict, e.g., duplicate data */
  CONFLICT(409, "Conflict"),

  /** Validation failed */
  VALIDATION_ERROR(422, "Validation Error"),

  /** Service unavailable */
  SERVICE_UNAVAILABLE(503, "Service Unavailable"),

  /** Internal server error */
  INTERNAL_ERROR(500, "Internal Server Error");

  private final int status;
  private final String message;

  ResponseCode(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
