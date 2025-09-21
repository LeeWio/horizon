package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;

/**
 * 用户不存在异常
 */
public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException() {
        super(ResponseCode.USER_NOT_FOUND);
    }
    
    public UserNotFoundException(String userId) {
        super(ResponseCode.USER_NOT_FOUND, "用户不存在，ID: " + userId, userId);
    }
    
    public UserNotFoundException(String field, String value) {
        super(ResponseCode.USER_NOT_FOUND, String.format("用户不存在，%s: %s", field, value), field, value);
    }
}
