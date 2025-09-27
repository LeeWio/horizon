package com.sunrizon.horizon.enums;

/**
 * System permissions enumeration.
 * <p>
 * Each enum represents a specific action in the system.
 */
public enum PermissionType {

  // ====================== Article / Post Management ======================
  ARTICLE_CREATE, // Create a new article
  ARTICLE_READ, // View article list or details
  ARTICLE_UPDATE, // Update existing article
  ARTICLE_DELETE, // Delete article
  ARTICLE_PUBLISH, // Publish article
  ARTICLE_ARCHIVE, // Archive article
  ARTICLE_UNPUBLISH, // Unpublish article (move back to draft)
  ARTICLE_TAG_MANAGE, // Manage article tags/categories

  // ====================== Category / Tag Management ======================
  CATEGORY_CREATE,
  CATEGORY_READ,
  CATEGORY_UPDATE,
  CATEGORY_DELETE,

  TAG_CREATE,
  TAG_READ,
  TAG_UPDATE,
  TAG_DELETE,

  // ====================== Role Management ======================
  ROLE_CREATE,
  ROLE_READ,
  ROLE_UPDATE,
  ROLE_DELETE,
  ROLE_ASSIGN, // Assign roles to users

  // ====================== User Management ======================
  USER_CREATE,
  USER_UPDATE,
  USER_DELETE,
  USER_VIEW,
  USER_SUSPEND, // Suspend/ban user
  USER_PASSWORD_RESET, // Reset user password

  // ====================== Comment Management ======================
  COMMENT_CREATE, // Post comments
  COMMENT_VIEW, // View comments
  COMMENT_MODERATE, // Moderate comments (approve/reject)
  COMMENT_DELETE, // Delete comments
  COMMENT_REPLY, // Reply to comments

  // ====================== Media / File Management ======================
  MEDIA_UPLOAD, // Upload images/videos
  MEDIA_READ, // View/list media files
  MEDIA_DELETE, // Delete media files

  // ====================== Page Management ======================
  PAGE_CREATE, // Create static pages
  PAGE_READ, // View static pages
  PAGE_UPDATE, // Update static pages
  PAGE_DELETE, // Delete static pages
  PAGE_PUBLISH, // Publish static pages

  // ====================== System & Site Settings ======================
  SITE_SETTINGS, // Access and modify site settings
  ROLE_MANAGE, // Manage role permissions
  THEME_MANAGE, // Manage site themes/layout
  PLUGIN_MANAGE, // Manage plugins/extensions
  MENU_MANAGE, // Manage navigation menus
  DASHBOARD_ACCESS, // Access dashboard/admin panel
  ANALYTICS_VIEW, // View site analytics/traffic
  BACKUP_RESTORE, // Backup and restore site data

  // ====================== Notification & Messaging ======================
  NOTIFICATION_SEND, // Send notifications
  NOTIFICATION_READ, // View notifications
  MESSAGE_MANAGE // Manage internal messages or inbox
}
