package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for changing user password
 */
@Data
public class ChangePasswordRequest {

  @NotBlank(message = "Old password is required")
  private String oldPassword;

  @NotBlank(message = "New password is required")
  private String newPassword;
}
