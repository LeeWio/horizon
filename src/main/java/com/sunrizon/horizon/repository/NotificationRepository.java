package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.enums.NotificationType;
import com.sunrizon.horizon.pojo.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Notification repository
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

  /**
   * Find all notifications for a user
   */
  Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

  /**
   * Find unread notifications for a user
   */
  Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(String userId, Boolean isRead, Pageable pageable);

  /**
   * Find notifications by type for a user
   */
  Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, NotificationType type, Pageable pageable);

  /**
   * Count unread notifications for a user
   */
  Long countByUserIdAndIsRead(String userId, Boolean isRead);

  /**
   * Mark all notifications as read for a user
   */
  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
  int markAllAsRead(@Param("userId") String userId);

  /**
   * Delete all notifications for a user
   */
  void deleteByUserId(String userId);

  /**
   * Delete read notifications for a user
   */
  @Modifying
  @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.isRead = true")
  int deleteReadNotifications(@Param("userId") String userId);
}
