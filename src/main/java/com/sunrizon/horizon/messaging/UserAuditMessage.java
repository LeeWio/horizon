package com.sunrizon.horizon.messaging;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User audit notification message
 * Sent when a new user registers and needs admin approval
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuditMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * User ID to be audited
   */
  private String userId;

  /**
   * Username
   */
  private String username;

  /**
   * Email
   */
  private String email;

  /**
   * Registration time
   */
  private LocalDateTime registeredAt;
}
