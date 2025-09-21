package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;
import lombok.Getter;

/**
 * 业务异常基类
 * 用于处理业务逻辑中的异常情况
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ResponseCode responseCode;
    private final Object[] args;
    
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.args = null;
    }
    
    public BusinessException(ResponseCode responseCode, Object... args) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.args = args;
    }
    
    public BusinessException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
        this.args = null;
    }
    
    public BusinessException(ResponseCode responseCode, String message, Object... args) {
        super(message);
        this.responseCode = responseCode;
        this.args = args;
    }
    
    public BusinessException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.responseCode = responseCode;
        this.args = null;
    }
    
    public BusinessException(ResponseCode responseCode, Throwable cause, Object... args) {
        super(responseCode.getMessage(), cause);
        this.responseCode = responseCode;
        this.args = args;
    }
}
