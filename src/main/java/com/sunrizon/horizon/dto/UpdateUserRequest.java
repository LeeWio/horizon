package com.sunrizon.horizon.dto;

import java.util.Set;
import lombok.Data;

/**
 * DTO for updating an existing user.
 * <p>
 * Contains the user ID and fields to be updated.
 */
@Data
public class UpdateUserRequest {

  /** User ID to identify the user to update */
  private String uid;

  /** New username (optional) */
  private String username;

  /** New email (optional) */
  private String email;

  /** New password (optional) */
  private String password;

  /** Optional role IDs to update user's roles */
  private Set<String> roleIds;
}
