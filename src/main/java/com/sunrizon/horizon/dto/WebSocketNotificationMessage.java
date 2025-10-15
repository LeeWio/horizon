package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket Notification Message DTO
 * 
 * WebSocket推送的通知消息数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketNotificationMessage {

  /**
   * 通知ID
   */
  private String nid;

  /**
   * 通知类型
   */
  private NotificationType type;

  /**
   * 通知标题
   */
  private String title;

  /**
   * 通知内容
   */
  private String content;

  /**
   * 关联资源ID（文章ID/评论ID等）
   */
  private String relatedId;

  /**
   * 发送者ID
   */
  private String senderId;

  /**
   * 发送者用户名
   */
  private String senderUsername;

  /**
   * 发送者头像
   */
  private String senderAvatar;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 未读通知数量（可选）
   */
  private Long unreadCount;
}
