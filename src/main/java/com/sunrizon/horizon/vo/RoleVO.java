
package com.sunrizon.horizon.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RoleVO
 *
 * A view object representing role information returned to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {

  /**
   * Unique ID of the role.
   */
  private String rid;

  /**
   * Name of the role.
   */
  private String name;

  /**
   * Description of the role.
   */
  private String description;

  /**
   * Time when the role was created.
   */
  private LocalDateTime createdAt;

  /**
   * Time when the role was last updated.
   */
  private LocalDateTime updatedAt;
}
