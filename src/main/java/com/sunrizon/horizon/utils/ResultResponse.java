package com.sunrizon.horizon.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sunrizon.horizon.enums.ResponseCode;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Unified API response wrapper.
 * <p>
 * Best Practice: Use this ONLY for successful responses.
 * For errors, throw BusinessException and let GlobalExceptionHandler handle it.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse<T> {

  /** Business status code */
  private final int code;
  
  /** Response message */
  private final String message;
  
  /** Response data (null for errors) */
  private final T data;
  
  /** Response timestamp */
  private final LocalDateTime timestamp;

  private ResultResponse(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = LocalDateTime.now();
  }

  // ==================== Success Methods ====================
  
  /**
   * Create success response with default SUCCESS code.
   */
  public static <T> ResultResponse<T> success() {
    return new ResultResponse<>(ResponseCode.SUCCESS.getStatus(), 
        ResponseCode.SUCCESS.getMessage(), null);
  }

  /**
   * Create success response with data.
   */
  public static <T> ResultResponse<T> success(T data) {
    return new ResultResponse<>(ResponseCode.SUCCESS.getStatus(), 
        ResponseCode.SUCCESS.getMessage(), data);
  }

  /**
   * Create success response with custom code and data.
   */
  public static <T> ResultResponse<T> success(ResponseCode responseCode, T data) {
    return new ResultResponse<>(responseCode.getStatus(), 
        responseCode.getMessage(), data);
  }

  /**
   * Create success response with custom code (no data).
   */
  public static <T> ResultResponse<T> success(ResponseCode responseCode) {
    return new ResultResponse<>(responseCode.getStatus(), 
        responseCode.getMessage(), null);
  }

  // ==================== Error Methods (Deprecated) ====================
  
  /**
   * @deprecated Use throw new BusinessException(responseCode) instead.
   * This method is kept for backward compatibility only.
   */
  @Deprecated
  public static <T> ResultResponse<T> error(ResponseCode responseCode) {
    return new ResultResponse<>(responseCode.getStatus(), 
        responseCode.getMessage(), null);
  }

  /**
   * @deprecated Use throw new BusinessException(responseCode, message) instead.
   * This method is kept for backward compatibility only.
   */
  @Deprecated
  public static <T> ResultResponse<T> error(ResponseCode responseCode, String message) {
    return new ResultResponse<>(responseCode.getStatus(), message, null);
  }

  // ==================== Utility Methods ====================
  
  /**
   * Check if this response represents success.
   */
  public boolean isSuccess() {
    return code == ResponseCode.SUCCESS.getStatus();
  }
}
