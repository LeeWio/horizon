package com.sunrizon.horizon.enums;

import lombok.Getter;

/**
 * Standard API response codes.
 * Centralized management of common HTTP status codes and business-specific
 * error codes.
 */
@Getter
public enum ResponseCode {

  // ==================== Success ====================
  SUCCESS(200, "Request processed successfully"),

  // ==================== Client Errors (4xx) ====================
  BAD_REQUEST(400, "Invalid request parameters"),
  UNAUTHORIZED(401, "Authentication required"),
  FORBIDDEN(403, "Access denied"),
  NOT_FOUND(404, "Resource not found"),
  METHOD_NOT_ALLOWED(405, "HTTP method not allowed"),
  CONFLICT(409, "Resource conflict"),
  VALIDATION_ERROR(422, "Validation failed"),
  TOO_MANY_REQUESTS(429, "Too many requests"),

  // ==================== Server Errors (5xx) ====================
  INTERNAL_ERROR(500, "Internal server error"),
  SERVICE_UNAVAILABLE(503, "Service temporarily unavailable"),
  FAILURE(500, "Operation failed"),

  // ==================== User Errors ====================
  USER_NOT_FOUND(1001, "User not found"),
  USER_ALREADY_EXISTS(1002, "User already exists"),
  USER_INVALID_STATUS(1003, "Invalid user status"),
  USER_PASSWORD_INCORRECT(1004, "Incorrect password"),
  USER_ACCOUNT_DISABLED(1005, "User account disabled"),
  USER_ACCOUNT_LOCKED(1006, "User account locked"),
  USER_EMAIL_EXISTS(1007, "Email already registered"),
  USER_USERNAME_EXISTS(1008, "Username already taken"),
  USER_EMAIL_INVAILD(1009, "Invalid email format"),
  USER_CREATED(10010, "User created successfully"),
  USER_PASSWORD_REQUIRED(10011, "Password cannot be empty"),
  LOGIN_SUCCESS(10012, "Login successfully"),

  // ==================== Article Errors ====================
  ARTICLE_NOT_FOUND(2001, "Article not found"),
  ARTICLE_INVALID_STATUS(2002, "Invalid article status"),
  ARTICLE_PERMISSION_DENIED(2003, "No permission to modify this article"),
  ARTICLE_TITLE_EXISTS(2004, "Article title already exists"),
  ARTICLE_SLUG_EXISTS(2005, "Article slug already exists"),

  // ==================== Category Errors ====================
  CATEGORY_NOT_FOUND(3001, "Category not found"),
  CATEGORY_NAME_EXISTS(3002, "Category name already exists"),
  CATEGORY_HAS_ARTICLES(3003, "Cannot delete category containing articles"),

  // ==================== Tag Errors ====================
  TAG_NOT_FOUND(4001, "Tag not found"),
  TAG_NAME_EXISTS(4002, "Tag name already exists"),

  // ==================== Role Errors ====================
  ROLE_NOT_FOUND(5001, "Role not found"),
  ROLE_NAME_EXISTS(5002, "Role name already exists"),
  ROLE_IN_USE(5003, "Role is currently in use"),
  ROLE_IDS_NOT_FOUND(5004, "Some roles do not exist"),

  // ==================== Permission Errors ====================
  PERMISSION_DENIED(6001, "Insufficient permissions"),

  // ==================== File Errors ====================
  FILE_NOT_FOUND(7001, "File not found"),
  FILE_UPLOAD_FAILED(7002, "File upload failed"),
  FILE_TYPE_NOT_SUPPORTED(7003, "Unsupported file type"),
  FILE_SIZE_EXCEEDED(7004, "File size exceeds limit"),

  // ==================== External Services ====================
  EMAIL_SEND_FAILED(8001, "Failed to send email"),
  SMS_SEND_FAILED(8002, "Failed to send SMS"),
  THIRD_PARTY_SERVICE_ERROR(8003, "Third-party service error"),

  // ==================== User Management ====================
  USER_ID_CANNOT_BE_EMPTY(9001, "User ID cannot be empty"),
  USER_HAS_BEEN_DELETED_CANNOT_UPDATE(9002, "User has been deleted and cannot be updated."),
  USER_STATUS_ALREADY(9003, "User status is already %s"),
  USER_STATUS_UPDATED_SUCCESSFULLY(9004, "User status updated successfully to %s"),
  CURRENT_USER_NOT_FOUND(9005, "Current user not found"),
  CANNOT_DELETE_CURRENT_USER(9006, "Cannot delete the current user"),
  USER_ALREADY_MARKED_DELETED(9007, "User is already marked as deleted"),
  USER_DELETED_SUCCESSFULLY(9008, "User deleted successfully"),
  USER_UPDATED_SUCCESSFULLY(9009, "User updated successfully"),

  // ==================== Role Management ====================
  ROLE_NAME_CANNOT_BE_EMPTY(9101, "Role name cannot be empty."),
  ROLE_ID_CANNOT_BE_EMPTY(9102, "Role ID cannot be empty"),
  ROLE_DELETED_SUCCESSFULLY(9103, "Role deleted successfully"),
  USER_ID_MUST_BE_PROVIDED(9104, "User ID must be provided"),
  ROLE_IDS_MUST_BE_PROVIDED(9105, "Role IDs must be provided"),
  SOME_ROLES_DO_NOT_EXIST(9106, "Some roles do not exist"),
  ROLES_ASSIGNED_SUCCESSFULLY(9107, "Roles assigned successfully"),
  SOME_PERMISSIONS_DO_NOT_EXIST(9108, "Some permissions do not exist"),

  // ==================== Permission Management ====================
  PERMISSION_NAME_CANNOT_BE_NULL(9201, "Permission name cannot be null"),
  PERMISSION_NAME_ALREADY_EXISTS(9202, "Permission name already exists"),
  PERMISSION_DELETED_SUCCESSFULLY(9203, "Permission deleted successfully"),
  PERMISSION_NOT_FOUND(9204, "Permission not found for ID: %s"),

  // ==================== User Detail Service ====================
  USER_NOT_FOUND_WITH_EMAIL(9301, "User not found with email: %s"),

  // ==================== OTP Verification ====================
  INVALID_OTP(9401, "Invalid OTP code"),
  OTP_EXPIRED(9402, "OTP code has expired"),
  OTP_VERIFIED(9403, "OTP verified successfully");

  private final int status;
  private final String message;

  ResponseCode(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
