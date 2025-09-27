package com.sunrizon.horizon.enums;

/**
 * RoleType defines the fixed user roles in the system.
 * <p>
 * ADMIN - System administrator with full access.
 * EDITOR - Content editor with permissions to create and manage articles.
 * USER - Regular user with basic access to the system.
 */
public enum RoleType {
  SUPER_ADMIN, // 超级管理员
  ADMIN, // 管理员
  AUTHOR, // 作者/编辑
  CONTRIBUTOR, // 协作者
  SUBSCRIBER, // 订阅用户
  DEMO,
  GUEST; // 游客
}
