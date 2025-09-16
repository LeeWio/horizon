package com.sunrizon.horizon.vo;

import java.util.Set;
import lombok.Data;

/**
 * Value Object representing a user for front-end responses.
 * <p>
 * This object is returned to the front-end instead of exposing the entity
 * directly.
 */
@Data
public class UserVO {

  /** User ID */
  private String uid;

  /** Username */
  private String username;

  /** Email */
  private String email;

  /** Avatar URL or path */
  private String avatar;

  /** Set of role names assigned to the user */
  private Set<String> roles;

  /** User account status (e.g., ACTIVE, BANNED) */
  private String status;
}
