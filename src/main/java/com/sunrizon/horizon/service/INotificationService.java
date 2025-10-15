package com.sunrizon.horizon.service;

import com.sunrizon.horizon.enums.NotificationType;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.NotificationVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Notification service interface
 */
public interface INotificationService {

  /**
   * Create a notification
   *
   * @param userId    Receiver user ID
   * @param type      Notification type
   * @param title     Notification title
   * @param content   Notification content
   * @param relatedId Related resource ID
   * @param senderId  Sender user ID
   * @return ResultResponse with created NotificationVO
   */
  ResultResponse<NotificationVO> createNotification(String userId, NotificationType type, String title,
      String content, String relatedId, String senderId);

  /**
   * Get all notifications for current user
   *
   * @param userId   User ID
   * @param pageable Pagination info
   * @return ResultResponse with page of NotificationVO
   */
  ResultResponse<Page<NotificationVO>> getNotifications(String userId, Pageable pageable);

  /**
   * Get unread notifications for current user
   *
   * @param userId   User ID
   * @param pageable Pagination info
   * @return ResultResponse with page of NotificationVO
   */
  ResultResponse<Page<NotificationVO>> getUnreadNotifications(String userId, Pageable pageable);

  /**
   * Get notifications by type
   *
   * @param userId   User ID
   * @param type     Notification type
   * @param pageable Pagination info
   * @return ResultResponse with page of NotificationVO
   */
  ResultResponse<Page<NotificationVO>> getNotificationsByType(String userId, NotificationType type, Pageable pageable);

  /**
   * Get unread notification count
   *
   * @param userId User ID
   * @return ResultResponse with unread count
   */
  ResultResponse<Long> getUnreadCount(String userId);

  /**
   * Mark a notification as read
   *
   * @param userId User ID
   * @param nid    Notification ID
   * @return ResultResponse with success message
   */
  ResultResponse<String> markAsRead(String userId, String nid);

  /**
   * Mark all notifications as read
   *
   * @param userId User ID
   * @return ResultResponse with success message
   */
  ResultResponse<String> markAllAsRead(String userId);

  /**
   * Delete a notification
   *
   * @param userId User ID
   * @param nid    Notification ID
   * @return ResultResponse with success message
   */
  ResultResponse<String> deleteNotification(String userId, String nid);

  /**
   * Delete all read notifications
   *
   * @param userId User ID
   * @return ResultResponse with success message
   */
  ResultResponse<String> deleteReadNotifications(String userId);

  /**
   * Delete all notifications
   *
   * @param userId User ID
   * @return ResultResponse with success message
   */
  ResultResponse<String> deleteAllNotifications(String userId);
}
