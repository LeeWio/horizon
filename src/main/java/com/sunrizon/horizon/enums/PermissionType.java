package com.sunrizon.horizon.enums;

/**
 * System permissions enumeration.
 * <p>
 * Each enum represents a specific action in the system.
 */
public enum PermissionType {
  // Article permissions
  ARTICLE_CREATE,
  ARTICLE_READ,
  ARTICLE_UPDATE,
  ARTICLE_DELETE,
  ARTICLE_PUBLISH,
  ARTICLE_ARCHIVE,

  // User permissions
  USER_CREATE,
  USER_READ,
  USER_UPDATE,
  USER_DELETE,
  USER_ASSIGN_ROLE,

  // Role permissions
  ROLE_CREATE,
  ROLE_READ,
  ROLE_UPDATE,
  ROLE_DELETE
}
