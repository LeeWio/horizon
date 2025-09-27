package com.sunrizon.horizon.enums;

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

  // TODO: 是否替换 UserServiceImpl 的 updateStatus 逻辑
  public boolean transformTo(UserStatus status) {
    switch (status) {
      case ACTIVE:
        if (this == INACTIVE || status == UserStatus.BANNED) {
          return true;
        }
        break;
      case INACTIVE:
        break;
      case BANNED:
        break;
      case DELETED:
        break;
      case PENDING:
        break;
    }
    return true;
  }
}
