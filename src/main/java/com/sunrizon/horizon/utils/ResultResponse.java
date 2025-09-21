package com.sunrizon.horizon.utils;

import com.sunrizon.horizon.enums.ResponseCode;

import lombok.Getter;
import lombok.Setter;

/**
 * 标准API响应包装器
 * 统一API响应格式，包含状态码、消息和数据
 *
 * @param <T> 响应数据的类型
 */
@Getter
@Setter
public class ResultResponse<T> {

  /** 状态码，如200表示成功，500表示失败 */
  private int status;

  /** 响应消息 */
  private String message;

  /** 响应数据 */
  private T data;

  /**
   * 使用ResponseCode构造响应
   *
   * @param responseCode 包含状态码和消息的ResponseCode
   */
  public ResultResponse(ResponseCode responseCode) {
    if (responseCode == null) {
      this.status = ResponseCode.FAILURE.getStatus();
      this.message = ResponseCode.FAILURE.getMessage();
    } else {
      this.status = responseCode.getStatus();
      this.message = responseCode.getMessage();
    }
  }

  /**
   * 使用ResponseCode和数据构造响应
   *
   * @param responseCode 包含状态码和消息的ResponseCode
   * @param data         响应数据
   */
  public ResultResponse(ResponseCode responseCode, T data) {
    this(responseCode);
    this.data = data;
  }

  /**
   * 使用自定义状态码和消息构造响应
   *
   * @param status  状态码
   * @param message 消息
   */
  public ResultResponse(int status, String message) {
    this.status = status;
    this.message = message;
  }

  /**
   * 使用自定义状态码、消息和数据构造响应
   *
   * @param status  状态码
   * @param message 消息
   * @param data    响应数据
   */
  public ResultResponse(int status, String message, T data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  // ==================== 成功响应方法 ====================

  /**
   * 成功响应，使用默认SUCCESS状态码
   */
  public static <T> ResultResponse<T> success() {
    return new ResultResponse<>(ResponseCode.SUCCESS);
  }

  /**
   * 成功响应，包含数据
   */
  public static <T> ResultResponse<T> success(T data) {
    return new ResultResponse<>(ResponseCode.SUCCESS, data);
  }

  /**
   * 成功响应，包含自定义消息和数据
   */
  public static <T> ResultResponse<T> success(String message, T data) {
    ResultResponse<T> response = new ResultResponse<>(ResponseCode.SUCCESS, data);
    response.setMessage(message);
    return response;
  }

  /**
   * 成功响应，只包含自定义消息
   */
  public static <T> ResultResponse<T> success(String message) {
    ResultResponse<T> response = new ResultResponse<>(ResponseCode.SUCCESS);
    response.setMessage(message);
    return response;
  }

  // ==================== 错误响应方法 ====================

  /**
   * 错误响应，使用默认FAILURE状态码
   */
  public static <T> ResultResponse<T> error() {
    return new ResultResponse<>(ResponseCode.FAILURE);
  }

  /**
   * 错误响应，包含自定义消息
   */
  public static <T> ResultResponse<T> error(String message) {
    ResultResponse<T> response = new ResultResponse<>(ResponseCode.FAILURE);
    response.setMessage(message);
    return response;
  }

  /**
   * 错误响应，使用指定的ResponseCode
   */
  public static <T> ResultResponse<T> error(ResponseCode responseCode) {
    return new ResultResponse<>(responseCode);
  }

  /**
   * 错误响应，使用指定的ResponseCode和自定义消息
   */
  public static <T> ResultResponse<T> error(ResponseCode responseCode, String message) {
    ResultResponse<T> response = new ResultResponse<>(responseCode);
    response.setMessage(message);
    return response;
  }

  /**
   * 错误响应，使用指定的ResponseCode和数据
   */
  public static <T> ResultResponse<T> error(ResponseCode responseCode, T data) {
    return new ResultResponse<>(responseCode, data);
  }

  /**
   * 错误响应，使用自定义状态码和消息
   */
  public static <T> ResultResponse<T> error(int status, String message) {
    return new ResultResponse<>(status, message);
  }

  /**
   * 错误响应，使用自定义状态码、消息和数据
   */
  public static <T> ResultResponse<T> error(int status, String message, T data) {
    return new ResultResponse<>(status, message, data);
  }

  // ==================== 便利方法 ====================

  /**
   * 判断是否成功
   */
  public boolean isSuccess() {
    return this.status >= 200 && this.status < 300;
  }

  /**
   * 判断是否失败
   */
  public boolean isError() {
    return !isSuccess();
  }

  /**
   * 判断是否有数据
   */
  public boolean hasData() {
    return this.data != null;
  }

  /**
   * 获取数据，如果为null则返回默认值
   */
  public T getDataOrDefault(T defaultValue) {
    return this.data != null ? this.data : defaultValue;
  }
}
