package com.sunrizon.horizon.dto;

import java.util.Set;
import lombok.Data;

/**
 * DTO for creating a new user.
 * <p>
 * This object is received from the front-end when creating a user.
 */
@Data
public class CreateUserRequest {

  /** Username of the new user */
  private String username;

  /** Email of the new user */
  private String email;

  /** Password of the new user */
  private String password;

  /** Optional role IDs to assign to the new user */
  private Set<String> roleIds;
}
