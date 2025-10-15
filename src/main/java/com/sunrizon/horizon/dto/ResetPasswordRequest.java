package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for password reset request
 */
@Data
public class ResetPasswordRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "OTP code is required")
  private String otp;

  @NotBlank(message = "New password is required")
  private String newPassword;
}
