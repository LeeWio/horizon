package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.enums.NotificationType;
import com.sunrizon.horizon.service.INotificationService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * Notification Controller
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notification")
@Tag(name = "Notification Management", description = "Notification management APIs")
@Slf4j
public class NotificationController {

  @Resource
  private INotificationService notificationService;

  @Resource
  private SecurityContextUtil securityContextUtil;

  /**
   * Get all notifications for current user
   */
  @GetMapping
  @Operation(summary = "Get all notifications")
  public ResultResponse<Page<NotificationVO>> getNotifications(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.getNotifications(userId, pageable);
  }

  /**
   * Get unread notifications
   */
  @GetMapping("/unread")
  @Operation(summary = "Get unread notifications")
  public ResultResponse<Page<NotificationVO>> getUnreadNotifications(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.getUnreadNotifications(userId, pageable);
  }

  /**
   * Get notifications by type
   */
  @GetMapping("/type/{type}")
  @Operation(summary = "Get notifications by type")
  public ResultResponse<Page<NotificationVO>> getNotificationsByType(
      @PathVariable NotificationType type,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.getNotificationsByType(userId, type, pageable);
  }

  /**
   * Get unread notification count
   */
  @GetMapping("/unread/count")
  @Operation(summary = "Get unread notification count")
  public ResultResponse<Long> getUnreadCount() {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.getUnreadCount(userId);
  }

  /**
   * Mark a notification as read
   */
  @PutMapping("/{nid}/read")
  @Operation(summary = "Mark notification as read")
  public ResultResponse<String> markAsRead(@PathVariable String nid) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.markAsRead(userId, nid);
  }

  /**
   * Mark all notifications as read
   */
  @PutMapping("/read-all")
  @Operation(summary = "Mark all notifications as read")
  public ResultResponse<String> markAllAsRead() {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.markAllAsRead(userId);
  }

  /**
   * Delete a notification
   */
  @DeleteMapping("/{nid}")
  @Operation(summary = "Delete a notification")
  public ResultResponse<String> deleteNotification(@PathVariable String nid) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.deleteNotification(userId, nid);
  }

  /**
   * Delete all read notifications
   */
  @DeleteMapping("/read")
  @Operation(summary = "Delete all read notifications")
  public ResultResponse<String> deleteReadNotifications() {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.deleteReadNotifications(userId);
  }

  /**
   * Delete all notifications
   */
  @DeleteMapping("/all")
  @Operation(summary = "Delete all notifications")
  public ResultResponse<String> deleteAllNotifications() {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return notificationService.deleteAllNotifications(userId);
  }
}
