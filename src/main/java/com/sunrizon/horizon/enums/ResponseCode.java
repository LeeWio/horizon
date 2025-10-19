package com.sunrizon.horizon.enums;

import lombok.Getter;

/**
 * Standard API response codes.
 * <p>
 * Code Convention:
 * - 200: Success (generic)
 * - 400-499: Client errors (HTTP standard codes)
 * - 500-599: Server errors (HTTP standard codes)
 * - 1000-9999: Business errors (custom codes by module)
 *   - 1xxx: User module
 *   - 2xxx: Article module
 *   - 3xxx: Category/Series module
 *   - 4xxx: Tag module
 *   - 5xxx: Role module
 *   - 6xxx: Permission module
 *   - 7xxx: File module
 *   - 8xxx: External services
 *   - 9xxx: System management
 *   - 10xxx: Social features (comment, interaction, follow, notification)
 * <p>
 * Best Practice:
 * - Use SUCCESS for generic success responses
 * - Use specific success codes (e.g., USER_CREATED) for operations that need explicit feedback
 * - Use business error codes (1xxx-9xxx) for domain-specific errors
 * - Use HTTP error codes (4xx, 5xx) for protocol-level errors
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
  ARTICLE_CREATED(2006, "Article created successfully"),
  ARTICLE_UPDATED_SUCCESSFULLY(2007, "Article updated successfully"),
  ARTICLE_DELETED_SUCCESSFULLY(2008, "Article deleted successfully"),
  ARTICLE_ID_CANNOT_BE_EMPTY(2009, "Article ID cannot be empty"),
  ARTICLE_PUBLISH_TIME_REQUIRED_FOR_SCHEDULED(2010, "Publish time is required for scheduled articles"),

  // ==================== Category Errors ====================
  CATEGORY_NOT_FOUND(3001, "Category not found"),
  CATEGORY_NAME_EXISTS(3002, "Category name already exists"),
  CATEGORY_HAS_ARTICLES(3003, "Cannot delete category containing articles"),
  CATEGORY_CREATED(3004, "Category created successfully"),
  CATEGORY_DELETED_SUCCESSFULLY(3005, "Category deleted successfully"),
  CATEGORY_UPDATED_SUCCESSFULLY(3006, "Category updated successfully"),
  CATEGORY_ID_CANNOT_BE_EMPTY(3007, "Category ID cannot be empty"),
  CATEGORY_NAME_REQUIRED(3008, "Category name is required"),
  CATEGORY_SLUG_REQUIRED(3009, "Category slug is required"),
  CATEGORY_NAME_TOO_LONG(3010, "Category name must not exceed 50 characters"),
  CATEGORY_SLUG_TOO_LONG(3011, "Category slug must not exceed 100 characters"),
  CATEGORY_DESCRIPTION_TOO_LONG(3012, "Category description must not exceed 200 characters"),
  CATEGORY_PARENT_NOT_FOUND(3013, "Parent category not found with ID: %s"),
  CATEGORY_NAME_INVALID(3014, "Invalid category name"),
  CATEGORY_SLUG_INVALID(3015, "Invalid category slug"),

  // ==================== Column Errors ====================
  COLUMN_NOT_FOUND(3101, "Column not found"),
  COLUMN_NAME_EXISTS(3102, "Column name already exists"),
  COLUMN_CREATED(3103, "Column created successfully"),
  COLUMN_DELETED_SUCCESSFULLY(3104, "Column deleted successfully"),
  COLUMN_UPDATED_SUCCESSFULLY(3105, "Column updated successfully"),
  SERIES_NOT_FOUND(3101, "Series not found"),
  SERIES_NAME_EXISTS(3102, "Series name already exists"),
  SERIES_CREATED(3103, "Series created successfully"),
  SERIES_DELETED_SUCCESSFULLY(3104, "Series deleted successfully"),
  SERIES_UPDATED_SUCCESSFULLY(3105, "Series updated successfully"),
  SERIES_ID_CANNOT_BE_EMPTY(3106, "Series ID cannot be empty"),
  SERIES_NAME_REQUIRED(3107, "Series name is required"),
  SERIES_SLUG_REQUIRED(3108, "Series slug is required"),

  // ==================== Tag Errors ====================
  TAG_NOT_FOUND(4001, "Tag not found"),
  TAG_NAME_EXISTS(4002, "Tag name already exists"),
  TAG_SLUG_EXISTS(4003, "Tag slug already exists"),
  TAG_CREATED(4004, "Tag created successfully"),
  TAG_DELETED_SUCCESSFULLY(4005, "Tag deleted successfully"),
  TAG_UPDATED_SUCCESSFULLY(4006, "Tag updated successfully"),
  TAG_ID_CANNOT_BE_EMPTY(4007, "Tag ID cannot be empty"),

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
  FILE_UPLOAD_SUCCESS(7005, "File uploaded successfully"),
  FILE_DELETED_SUCCESSFULLY(7006, "File deleted successfully"),
  FILE_IS_EMPTY(7007, "File is empty"),
  FILE_ID_CANNOT_BE_EMPTY(7008, "File ID cannot be empty"),

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
  OTP_VERIFIED(9403, "OTP verified successfully"),

  // ==================== Comment Errors ====================
  COMMENT_NOT_FOUND(10001, "Comment not found"),
  COMMENT_CREATED(10002, "Comment created successfully"),
  COMMENT_DELETED_SUCCESSFULLY(10003, "Comment deleted successfully"),
  COMMENT_ID_CANNOT_BE_EMPTY(10004, "Comment ID cannot be empty"),
  COMMENT_ARTICLE_NOT_FOUND(10005, "Article not found for comment"),
  COMMENT_PARENT_NOT_FOUND(10006, "Parent comment not found"),

  // ==================== Statistics Errors ====================
  READING_RECORDED(10101, "Reading recorded successfully"),
  STATS_NOT_FOUND(10102, "Statistics not found"),

  // ==================== Interaction Errors ====================
  ARTICLE_LIKED(10201, "Article liked successfully"),
  ARTICLE_UNLIKED(10202, "Article unliked successfully"),
  ARTICLE_FAVORITED(10203, "Article favorited successfully"),
  ARTICLE_UNFAVORITED(10204, "Article unfavorited successfully"),
  ARTICLE_SHARED(10205, "Article shared successfully"),
  INTERACTION_ALREADY_EXISTS(10206, "Interaction already exists"),
  INTERACTION_NOT_FOUND(10207, "Interaction not found"),

  // ==================== Follow Errors ====================
  USER_FOLLOWED(10301, "User followed successfully"),
  USER_UNFOLLOWED(10302, "User unfollowed successfully"),
  ALREADY_FOLLOWING(10303, "Already following this user"),
  NOT_FOLLOWING(10304, "Not following this user"),
  CANNOT_FOLLOW_SELF(10305, "Cannot follow yourself"),
  FOLLOW_USER_NOT_FOUND(10306, "User to follow not found"),

  // ==================== User Profile Errors ====================
  PROFILE_UPDATED(10321, "Profile updated successfully"),
  PASSWORD_UPDATED(10322, "Password updated successfully"),
  OLD_PASSWORD_INCORRECT(10323, "Old password is incorrect"),
  PASSWORD_TOO_WEAK(10324, "Password is too weak"),
  AVATAR_UPLOADED(10325, "Avatar uploaded successfully"),
  EMAIL_ALREADY_EXISTS(10326, "Email already exists"),
  USERNAME_ALREADY_EXISTS(10327, "Username already exists"),

  // ==================== Password Reset Errors ====================
  PASSWORD_RESET_EMAIL_SENT(10341, "Password reset email sent"),
  PASSWORD_RESET_SUCCESS(10342, "Password reset successfully"),
  RESET_TOKEN_INVALID(10343, "Reset token is invalid or expired"),
  RESET_TOKEN_EXPIRED(10344, "Reset token has expired"),

  // ==================== Notification Errors ====================
  NOTIFICATION_CREATED(10401, "Notification created successfully"),
  NOTIFICATION_MARKED_READ(10402, "Notification marked as read"),
  ALL_NOTIFICATIONS_MARKED_READ(10403, "All notifications marked as read"),
  NOTIFICATION_DELETED(10404, "Notification deleted successfully"),
  ALL_NOTIFICATIONS_DELETED(10405, "All notifications deleted"),
  NOTIFICATION_NOT_FOUND(10406, "Notification not found");

  private final int status;
  private final String message;

  ResponseCode(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
