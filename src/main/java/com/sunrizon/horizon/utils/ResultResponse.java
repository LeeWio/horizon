package com.sunrizon.horizon.utils;

import com.sunrizon.horizon.enums.ResponseCode;

import lombok.Getter;

@Getter
public class ResultResponse<T> {

  private final int status;
  private final String message;
  private final T data;

  private ResultResponse(ResponseCode responseCode, T data) {
    if (responseCode == null) {
      this.status = 500;
      this.message = "Unknown error";
    } else {
      this.status = responseCode.getStatus();
      this.message = responseCode.getMessage();
    }
    this.data = data;
  }

  public static <T> ResultResponse<T> success(ResponseCode responseCode) {
    return new ResultResponse<>(responseCode, null);
  }

  public static <T> ResultResponse<T> success(ResponseCode responseCode, T data) {
    return new ResultResponse<T>(responseCode, data);
  }

  public static <T> ResultResponse<T> success(T data) {
    return new ResultResponse<>(ResponseCode.SUCCESS, data);
  }

  public static <T> ResultResponse<T> error(ResponseCode responseCode) {
    return new ResultResponse<>(responseCode, null);
  }

  public static <T> ResultResponse<T> error(ResponseCode responseCode, T data) {
    return new ResultResponse<>(responseCode, data);
  }
}
