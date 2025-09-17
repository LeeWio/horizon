package com.sunrizon.horizon.dto;

import lombok.Data;

@Data
public class LoginUserRequest {

  /** Username of the user (eg. 3499508634@qq.com) */
  private String username;

  /** Password of the user */
  private String password;

  /** Email of the user */
  private String email;
}
