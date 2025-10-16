package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.utils.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Global exception handler for unified error responses.
 * <p>
 * This handler catches all exceptions thrown by controllers and services,
 * and converts them into standardized ResultResponse format.
 * <p>
 * Exception handling priority (from high to low):
 * 1. BusinessException - Custom business logic errors
 * 2. Validation exceptions - Request parameter validation errors
 * 3. Spring Security exceptions - Authentication/Authorization errors
 * 4. Spring MVC exceptions - HTTP request errors
 * 5. Generic exceptions - Unexpected errors
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // ==================== Business Exceptions ====================

  /**
   * Handle custom business exceptions.
   * These are thrown intentionally by service layer.
   */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK) // Business errors still return 200, error info in response body
  public ResultResponse<?> handleBusinessException(BusinessException e) {
    log.warn("Business exception: [{}] {}", e.getResponseCode().getStatus(), e.getErrorMessage());
    return ResultResponse.error(e.getResponseCode(), e.getErrorMessage());
  }

  // ==================== Validation Exceptions ====================

  /**
   * Handle @Valid validation exceptions (for @RequestBody).
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));
    
    log.warn("Validation failed: {}", errorMessage);
    return ResultResponse.error(ResponseCode.VALIDATION_ERROR, errorMessage);
  }

  /**
   * Handle @Validated validation exceptions (for method parameters).
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
    String errorMessage = e.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining("; "));
    
    log.warn("Constraint violation: {}", errorMessage);
    return ResultResponse.error(ResponseCode.VALIDATION_ERROR, errorMessage);
  }

  /**
   * Handle form data binding exceptions.
   */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultResponse<?> handleBindException(BindException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining("; "));
    
    log.warn("Bind exception: {}", errorMessage);
    return ResultResponse.error(ResponseCode.BAD_REQUEST, errorMessage);
  }

  // ==================== HTTP Request Exceptions ====================

  /**
   * Handle missing request parameter exceptions.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultResponse<?> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    String errorMessage = String.format("Missing required parameter: %s", e.getParameterName());
    log.warn(errorMessage);
    return ResultResponse.error(ResponseCode.BAD_REQUEST, errorMessage);
  }

  /**
   * Handle request parameter type mismatch exceptions.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultResponse<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
    String errorMessage = String.format("Parameter '%s' has invalid type", e.getName());
    log.warn("{}: expected {}, got {}", errorMessage, e.getRequiredType(), e.getValue());
    return ResultResponse.error(ResponseCode.BAD_REQUEST, errorMessage);
  }

  /**
   * Handle HTTP message not readable exceptions (e.g., malformed JSON).
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultResponse<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    log.warn("HTTP message not readable: {}", e.getMessage());
    return ResultResponse.error(ResponseCode.BAD_REQUEST, "Invalid request body format");
  }

  /**
   * Handle HTTP method not supported exceptions.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ResultResponse<?> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    String errorMessage = String.format("HTTP method %s not supported for this endpoint", e.getMethod());
    log.warn(errorMessage);
    return ResultResponse.error(ResponseCode.METHOD_NOT_ALLOWED, errorMessage);
  }

  /**
   * Handle 404 not found exceptions.
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResultResponse<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
    String errorMessage = String.format("Endpoint not found: %s %s", e.getHttpMethod(), e.getRequestURL());
    log.warn(errorMessage);
    return ResultResponse.error(ResponseCode.NOT_FOUND, errorMessage);
  }

  // ==================== Security Exceptions ====================

  /**
   * Handle Spring Security authentication exceptions.
   * Note: This may not be triggered if CustomAuthenticationEntryPoint is configured.
   */
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResultResponse<?> handleAuthenticationException(AuthenticationException e) {
    log.warn("Authentication failed: {}", e.getMessage());
    return ResultResponse.error(ResponseCode.UNAUTHORIZED, "Authentication required");
  }

  /**
   * Handle Spring Security access denied exceptions.
   * Note: This may not be triggered if CustomAccessDeniedHandler is configured.
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResultResponse<?> handleAccessDeniedException(AccessDeniedException e) {
    log.warn("Access denied: {}", e.getMessage());
    return ResultResponse.error(ResponseCode.FORBIDDEN, "Access denied");
  }

  // ==================== Generic Exceptions ====================

  /**
   * Handle all uncaught exceptions (fallback handler).
   * This should be the last resort for unexpected errors.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResultResponse<?> handleException(Exception e) {
    // Log full stack trace for debugging
    log.error("Unexpected error occurred", e);
    
    // Return generic error message to client (don't expose internal details)
    return ResultResponse.error(ResponseCode.INTERNAL_ERROR, "An unexpected error occurred");
  }
}
