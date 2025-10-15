package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Notification VO
 */
@Data
public class NotificationVO {

  private String nid;

  private String userId;

  private NotificationType type;

  private String title;

  private String content;

  private Boolean isRead;

  private String relatedId;

  private String senderId;

  /**
   * 发送者用户名（用于显示）
   */
  private String senderUsername;

  /**
   * 发送者头像
   */
  private String senderAvatar;

  private LocalDateTime createdAt;
}
