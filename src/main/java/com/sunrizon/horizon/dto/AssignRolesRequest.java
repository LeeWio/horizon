package com.sunrizon.horizon.dto;

import java.util.Set;
import lombok.Data;

/**
 * DTO for assigning roles to a user.
 */
@Data
public class AssignRolesRequest {

  /** User ID */
  private String uid;

  /** Set of role IDs to assign to the user */
  private Set<String> roleIds;
}
