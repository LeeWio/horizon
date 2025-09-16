package com.sunrizon.horizon.enums;

/**
 * RoleType defines the fixed user roles in the system.
 * <p>
 * ADMIN - System administrator with full access.
 * EDITOR - Content editor with permissions to create and manage articles.
 * USER - Regular user with basic access to the system.
 */
public enum RoleType {
  /** System administrator with full privileges */
  ADMIN,

  /** Content editor who can create, edit, and manage articles */
  EDITOR,

  /** Regular user with standard access */
  USER
}
