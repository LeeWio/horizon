
package com.sunrizon.horizon.dto;

import java.util.Set;

import com.sunrizon.horizon.constants.Constants;
import com.sunrizon.horizon.validation.PasswordStrength;
import com.sunrizon.horizon.validation.ValidationGroups;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户创建请求DTO
 * 用于接收客户端创建用户的请求数据
 */
@Data
public class CreateUserRequest {

  /**
   * 用户名
   */
  @NotBlank(message = "用户名不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  @Size(min = Constants.USERNAME_MIN_LENGTH, max = Constants.USERNAME_MAX_LENGTH, 
        message = "用户名长度必须在" + Constants.USERNAME_MIN_LENGTH + "-" + Constants.USERNAME_MAX_LENGTH + "个字符之间",
        groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  @Pattern(regexp = Constants.USERNAME_REGEX, message = "用户名只能包含字母、数字和下划线",
           groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  private String username;

  /**
   * 邮箱地址
   */
  @NotBlank(message = "邮箱不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  @Email(message = "邮箱格式不正确", groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  @Size(max = Constants.EMAIL_MAX_LENGTH, message = "邮箱长度不能超过" + Constants.EMAIL_MAX_LENGTH + "个字符",
        groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  private String email;

  /**
   * 密码
   */
  @NotBlank(message = "密码不能为空", groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH,
        message = "密码长度必须在" + Constants.PASSWORD_MIN_LENGTH + "-" + Constants.PASSWORD_MAX_LENGTH + "个字符之间",
        groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  @PasswordStrength(message = "密码强度不足，必须包含大小写字母、数字和特殊字符，长度至少8位",
                   groups = {ValidationGroups.Create.class, ValidationGroups.Register.class})
  private String password;

  /**
   * 头像URL（可选）
   */
  @Size(max = Constants.AVATAR_MAX_LENGTH, message = "头像URL长度不能超过" + Constants.AVATAR_MAX_LENGTH + "个字符")
  private String avatar;

  /**
   * 角色ID集合（可选）
   */
  private Set<String> roleIds;
}
