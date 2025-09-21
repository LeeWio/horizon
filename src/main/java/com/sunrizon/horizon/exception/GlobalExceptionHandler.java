package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.utils.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的所有异常，返回ResultResponse格式
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResultResponse<Object>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", e.getResponseCode().getStatus(), e.getMessage(), e);
        
        return ResponseEntity.badRequest()
            .body(ResultResponse.error(e.getResponseCode(), e.getMessage()));
    }

    /**
     * 处理用户不存在异常
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResultResponse<Object>> handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        log.warn("用户不存在异常: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResultResponse.error(ResponseCode.USER_NOT_FOUND, e.getMessage()));
    }

    /**
     * 处理文章不存在异常
     */
    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ResultResponse<Object>> handleArticleNotFoundException(ArticleNotFoundException e, HttpServletRequest request) {
        log.warn("文章不存在异常: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, e.getMessage()));
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponse<Object>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("参数验证异常: {}", e.getMessage());
        
        String errors = e.getBindingResult().getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        
        return ResponseEntity.badRequest()
            .body(ResultResponse.error(ResponseCode.VALIDATION_ERROR, "参数验证失败: " + errors));
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResultResponse<Object>> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("绑定异常: {}", e.getMessage());
        
        String errors = e.getBindingResult().getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        
        return ResponseEntity.badRequest()
            .body(ResultResponse.error(ResponseCode.VALIDATION_ERROR, "参数绑定失败: " + errors));
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResultResponse<Object>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("约束违反异常: {}", e.getMessage());
        
        String errors = e.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining("; "));
        
        return ResponseEntity.badRequest()
            .body(ResultResponse.error(ResponseCode.VALIDATION_ERROR, "约束违反: " + errors));
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultResponse<Object>> handleMissingParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数异常: {}", e.getMessage());
        
        return ResponseEntity.badRequest()
            .body(ResultResponse.error(ResponseCode.BAD_REQUEST, "缺少必需参数: " + e.getParameterName()));
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResultResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型不匹配异常: {}", e.getMessage());
        
        return ResponseEntity.badRequest()
            .body(ResultResponse.error(ResponseCode.BAD_REQUEST, "参数类型错误: " + e.getName()));
    }

    /**
     * 处理HTTP消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("HTTP消息不可读异常: {}", e.getMessage());
        
        return ResponseEntity.badRequest()
            .body(ResultResponse.error(ResponseCode.BAD_REQUEST, "请求体格式错误"));
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResultResponse<Object>> handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持异常: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ResultResponse.error(ResponseCode.METHOD_NOT_ALLOWED, "请求方法不支持: " + e.getMethod()));
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResultResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("404异常: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResultResponse.error(ResponseCode.NOT_FOUND, "请求的资源不存在"));
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResultResponse<Object>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("认证异常: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ResultResponse.error(ResponseCode.UNAUTHORIZED, "认证失败"));
    }

    /**
     * 处理凭证错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResultResponse<Object>> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.warn("凭证错误异常: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ResultResponse.error(ResponseCode.USER_PASSWORD_INCORRECT, "用户名或密码错误"));
    }

    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultResponse<Object>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("访问拒绝异常: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ResultResponse.error(ResponseCode.PERMISSION_DENIED, "权限不足"));
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse<Object>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResultResponse.error(ResponseCode.INTERNAL_ERROR, "系统内部错误"));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultResponse<Object>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResultResponse.error(ResponseCode.INTERNAL_ERROR, "系统运行时错误"));
    }
}