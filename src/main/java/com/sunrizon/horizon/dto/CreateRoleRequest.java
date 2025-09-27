package com.sunrizon.horizon.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new Role.
 * <p>
 * This object carries the required fields to create a role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

  /**
   * Name of the role (unique, human-readable).
   */
  @NotBlank(message = "Role name cannot be blank")
  @Size(max = 50, message = "Role name cannot exceed 50 characters")
  private String name;

  /**
   * A brief description of the role's purpose or permissions.
   */
  @Size(max = 255, message = "Description cannot exceed 255 characters")
  private String description;

  private Set<String> permissionIds;

}
