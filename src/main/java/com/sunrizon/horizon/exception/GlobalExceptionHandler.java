package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.utils.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for unified error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle business exceptions thrown by the service layer.
   */
  @ExceptionHandler(BusinessException.class)
  public ResultResponse<?> handleBusinessException(BusinessException e) {
    return ResultResponse.error(e.getResponseCode(), e.getMessage());
  }

}
