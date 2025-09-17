package com.sunrizon.horizon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object representing authentication information for front-end responses.
 * <p>
 * This object is returned to the front-end after a successful authentication
 * request instead of exposing the entity directly.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthVO {

  /** The generated authentication token (e.g., JWT) */
  private String authorization;

  /** User ID */
  private String uid;

  /** Email */
  private String email;

  /** Username */
  private String username;

}
