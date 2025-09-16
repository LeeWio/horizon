package com.sunrizon.horizon.utils;

import com.sunrizon.horizon.enums.ResponseCode;

import lombok.Getter;
import lombok.Setter;

/**
 * Standard API response wrapper.
 *
 * @param <T> The type of response data
 */
@Getter
@Setter
public class ResultResponse<T> {

  /** Status code, e.g., 200 for success, 500 for failure */
  private int status;

  /** Response message */
  private String message;

  /** Response data */
  private T data;

  /**
   * Constructor using ResponseCode.
   *
   * @param responseCode ResponseCode containing status and message
   */
  public ResultResponse(ResponseCode responseCode) {
    if (responseCode == null) {
      this.status = 500;
      this.message = "Unknown error";
    } else {
      this.status = responseCode.getStatus();
      this.message = responseCode.getMessage();
    }
  }

  /**
   * Constructor using ResponseCode and data.
   *
   * @param responseCode ResponseCode containing status and message
   * @param data         The response data
   */
  public ResultResponse(ResponseCode responseCode, T data) {
    this(responseCode);
    this.data = data;
  }

  // -------------------- Static helpers --------------------

  /** Success with data and default SUCCESS code */
  public static <T> ResultResponse<T> success(T data) {
    return new ResultResponse<>(ResponseCode.SUCCESS, data);
  }

  /** Success with custom message and data */
  public static <T> ResultResponse<T> success(String message, T data) {
    ResultResponse<T> response = new ResultResponse<>(ResponseCode.SUCCESS, data);
    response.setMessage(message);
    return response;
  }

  /** Generic error with default FAILURE code */
  public static <T> ResultResponse<T> error() {
    return new ResultResponse<>(ResponseCode.FAILURE);
  }

  /** Error with custom message */
  public static <T> ResultResponse<T> error(String message) {
    ResultResponse<T> response = new ResultResponse<>(ResponseCode.FAILURE);
    response.setMessage(message);
    return response;
  }

  /** Error using custom ResponseCode */
  public static <T> ResultResponse<T> error(ResponseCode responseCode) {
    return new ResultResponse<>(responseCode);
  }

  /** Error using custom ResponseCode and data */
  public static <T> ResultResponse<T> error(ResponseCode responseCode, T data) {
    return new ResultResponse<>(responseCode, data);
  }
}
