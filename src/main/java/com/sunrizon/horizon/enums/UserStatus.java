package com.sunrizon.horizon.enums;

import java.util.Optional;

/**
 * Enumeration representing user account status.
 */
public enum UserStatus {

  /** The user account is active and can be used normally. */
  ACTIVE,

  /** The user account is inactive and cannot be used. */
  INACTIVE,

  /** The user account is pending and waiting for administrator approval. */
  PENDING,

  /** The user account is banned due to violations or other reasons. */
  BANNED,

  /** The user account is deleted and can no longer be used. */
  DELETED;

  public Optional<String> transitionTo(UserStatus targetStatus) {
    switch (this) {
      case PENDING:
        if (targetStatus == ACTIVE || targetStatus == BANNED) {
          return Optional.empty();
        }
        return Optional.of("Pending user can only be activated or banned.");

      case ACTIVE:
        if (targetStatus == INACTIVE || targetStatus == BANNED) {
          return Optional.empty();
        }
        return Optional.of("Active user can only be set to inactive or banned.");

      case INACTIVE:
        if (targetStatus == ACTIVE || targetStatus == DELETED) {
          return Optional.empty();
        }
        return Optional.of("Inactive user can only be activated or deleted.");

      case BANNED:
        if (targetStatus == ACTIVE || targetStatus == DELETED) {
          return Optional.empty();
        }
        return Optional.of("Banned user can only be reactivated or deleted.");

      default:
        return Optional.of("Invalid user status.");
    }
  }
}
