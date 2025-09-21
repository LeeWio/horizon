package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.constants.UserConstants;
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
  @Size(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.EMAIL_MAX_LENGTH,
        message = "用户名或邮箱长度必须在" + UserConstants.USERNAME_MIN_LENGTH + "-" + UserConstants.EMAIL_MAX_LENGTH + "个字符之间",
        groups = {ValidationGroups.Login.class})
  private String username;

  /**
   * 密码
   */
  @NotBlank(message = "密码不能为空", groups = {ValidationGroups.Login.class})
  @Size(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH,
        message = "密码长度必须在" + UserConstants.PASSWORD_MIN_LENGTH + "-" + UserConstants.PASSWORD_MAX_LENGTH + "个字符之间",
        groups = {ValidationGroups.Login.class})
  private String password;

  /**
   * 记住我（可选）
   */
  private Boolean rememberMe = false;
}
