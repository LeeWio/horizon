package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.NotificationType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Notification entity
 * 通知实体类
 */
@Entity
@Table(name = "notification", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_is_read", columnList = "is_read"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Notification implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "nid", nullable = false, length = 36, updatable = false)
  private String nid;

  /**
   * 接收通知的用户ID
   */
  @Column(name = "user_id", nullable = false, length = 36)
  private String userId;

  /**
   * 通知类型：COMMENT, LIKE, FOLLOW, SYSTEM
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private NotificationType type;

  /**
   * 通知标题
   */
  @Column(name = "title", length = 200)
  private String title;

  /**
   * 通知内容
   */
  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  /**
   * 是否已读
   */
  @Column(name = "is_read", nullable = false)
  private Boolean isRead = false;

  /**
   * 关联的资源ID（如文章ID、评论ID等）
   */
  @Column(name = "related_id", length = 36)
  private String relatedId;

  /**
   * 触发通知的用户ID（谁触发的这个通知）
   */
  @Column(name = "sender_id", length = 36)
  private String senderId;

  /**
   * 创建时间
   */
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
