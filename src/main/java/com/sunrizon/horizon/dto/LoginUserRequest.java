package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.constants.UserConstants;
import com.sunrizon.horizon.validation.ValidationGroups;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Login request DTO
 * Allows login by username or email (one of them is required)
 */
@Data
public class LoginUserRequest {

  /**
   * Username
   */
  @Size(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.EMAIL_MAX_LENGTH, message = "Username length must be between "
      +
      UserConstants.USERNAME_MIN_LENGTH + " and " +
      UserConstants.EMAIL_MAX_LENGTH + " characters", groups = { ValidationGroups.Login.class })
  private String username;

  /**
   * Email
   */
  @Email(message = "Invalid email format", groups = { ValidationGroups.Login.class })
  @Size(max = UserConstants.EMAIL_MAX_LENGTH, message = "Email length must not exceed " + UserConstants.EMAIL_MAX_LENGTH
      + " characters", groups = { ValidationGroups.Login.class })
  private String email;

  /**
   * Password
   */
  @NotBlank(message = "Password cannot be blank", groups = { ValidationGroups.Login.class })
  @Size(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH, message = "Password length must be between "
      +
      UserConstants.PASSWORD_MIN_LENGTH + " and " +
      UserConstants.PASSWORD_MAX_LENGTH + " characters", groups = { ValidationGroups.Login.class })
  private String password;

  /**
   * Remember me (optional)
   */
  private Boolean rememberMe = false;
}
