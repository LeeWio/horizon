package com.sunrizon.horizon.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sunrizon.horizon.enums.ResponseCode;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Unified API response wrapper for SUCCESSFUL operations.
 *
 * <p>
 * <b>Best Practice:</b>
 * <ul>
 * <li>Use this class ONLY for successful responses.</li>
 * <li>For errors, throw {@code BusinessException} or other custom exceptions,
 * and let {@code @ControllerAdvice} handle them globally.</li>
 * <li>Never manually construct error responses with this class.</li>
 * </ul>
 *
 * @param <T> the type of response data
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse<T> {

  /** Business status code (e.g., 200, 201) */
  private final int code;

  /** Human-readable message */
  private final String message;

  /** Response payload (may be null if no data) */
  private final T data;

  /** ISO 8601 formatted timestamp of the response */
  private final LocalDateTime timestamp;

  private ResultResponse(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = LocalDateTime.now();
  }

  // ==================== Success Factories (Recommended) ====================

  /**
   * Returns a success response with default SUCCESS code and no data.
   */
  public static <T> ResultResponse<T> success() {
    return new ResultResponse<>(
        ResponseCode.SUCCESS.getStatus(),
        ResponseCode.SUCCESS.getMessage(),
        null);
  }

  /**
   * Returns a success response with custom message and no data (uses SUCCESS
   * code).
   */
  public static <T> ResultResponse<T> success(ResponseCode responseCode) {
    return new ResultResponse<>(
        responseCode.getStatus(),
        responseCode.getMessage(),
        null);
  }

  /**
   * Returns a success response with default SUCCESS code and given data.
   */
  public static <T> ResultResponse<T> success(T data) {
    return new ResultResponse<>(
        ResponseCode.SUCCESS.getStatus(),
        ResponseCode.SUCCESS.getMessage(),
        data);
  }

  /**
   * Returns a success response with custom message and data (uses SUCCESS code).
   */
  public static <T> ResultResponse<T> success(String customMessage, T data) {
    return new ResultResponse<>(
        ResponseCode.SUCCESS.getStatus(),
        customMessage,
        data);
  }

  // ==================== Business Success (Non-200 Success Codes)
  // ====================

  /**
   * Returns a business success response using a custom {@link ResponseCode}
   * (e.g., CREATED, ACCEPTED).
   * <p>
   * Only use this for non-error, non-200 success scenarios (e.g., 201 Created).
   */
  public static <T> ResultResponse<T> of(ResponseCode responseCode, T data) {
    if (responseCode == null) {
      throw new IllegalArgumentException("ResponseCode must not be null");
    }
    return new ResultResponse<>(
        responseCode.getStatus(),
        responseCode.getMessage(),
        data);
  }

  /**
   * Returns a business success response with custom message (uses given
   * {@link ResponseCode}).
   */
  public static <T> ResultResponse<T> of(ResponseCode responseCode, String customMessage, T data) {
    if (responseCode == null) {
      throw new IllegalArgumentException("ResponseCode must not be null");
    }
    return new ResultResponse<>(
        responseCode.getStatus(),
        customMessage != null ? customMessage : responseCode.getMessage(),
        data);
  }

  // ==================== Deprecated: Error Construction (Avoid!)
  // ====================

  /**
   * @deprecated Use {@code throw new BusinessException(responseCode)} instead.
   *             This method exists only for backward compatibility.
   */
  @Deprecated(since = "1.0", forRemoval = true)
  public static <T> ResultResponse<T> error(ResponseCode responseCode) {
    return new ResultResponse<>(
        responseCode.getStatus(),
        responseCode.getMessage(),
        null);
  }

  /**
   * @deprecated Use {@code throw new BusinessException(responseCode, message)}
   *             instead.
   */
  @Deprecated(since = "1.0", forRemoval = true)
  public static <T> ResultResponse<T> error(ResponseCode responseCode, String message) {
    return new ResultResponse<>(
        responseCode.getStatus(),
        message,
        null);
  }

}
