package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;

public class UsernameNotFoundException extends BusinessException {

  private static final long serialVersionUID = -6249691474254164720L;

  public UsernameNotFoundException() {
    super(ResponseCode.USER_NOT_FOUND);
  }

  public UsernameNotFoundException(String message) {
    super(ResponseCode.USER_NOT_FOUND, message);
  }

  public UsernameNotFoundException(Throwable throwable) {
    super(ResponseCode.USER_NOT_FOUND, throwable);
  }
}
