package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * User audit request DTO
 */
@Data
public class AuditUserRequest {

  @NotBlank(message = "User ID is required")
  private String userId;

  @NotNull(message = "Audit status is required")
  private UserStatus status;  // ACTIVE or BANNED

  private String reason;  // Rejection reason (optional)
}
