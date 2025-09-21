package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.constants.Constants;
import com.sunrizon.horizon.validation.ValidationGroups;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求DTO
 * 用于接收客户端登录请求数据
 */
@Data
public class LoginUserRequest {

  /**
   * 用户名或邮箱
   */
  @NotBlank(message = "用户名或邮箱不能为空", groups = {ValidationGroups.Login.class})
  @Size(min = Constants.USERNAME_MIN_LENGTH, max = Constants.EMAIL_MAX_LENGTH,
        message = "用户名或邮箱长度必须在" + Constants.USERNAME_MIN_LENGTH + "-" + Constants.EMAIL_MAX_LENGTH + "个字符之间",
        groups = {ValidationGroups.Login.class})
  private String username;

  /**
   * 密码
   */
  @NotBlank(message = "密码不能为空", groups = {ValidationGroups.Login.class})
  @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH,
        message = "密码长度必须在" + Constants.PASSWORD_MIN_LENGTH + "-" + Constants.PASSWORD_MAX_LENGTH + "个字符之间",
        groups = {ValidationGroups.Login.class})
  private String password;

  /**
   * 记住我（可选）
   */
  private Boolean rememberMe = false;
}
