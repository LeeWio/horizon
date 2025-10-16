package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.AuditReason;
import com.sunrizon.horizon.enums.AuditStatus;
import com.sunrizon.horizon.enums.ContentType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 内容审核记录实体类
 * 记录所有内容的审核历史
 */
@Table(name = "content_audit", indexes = {
    @Index(name = "idx_content_audit_content", columnList = "content_type, content_id"),
    @Index(name = "idx_content_audit_status", columnList = "status"),
    @Index(name = "idx_content_audit_submitter", columnList = "submitter_id"),
    @Index(name = "idx_content_audit_auditor", columnList = "auditor_id"),
    @Index(name = "idx_content_audit_created", columnList = "created_at")
})
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ContentAudit implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false, length = 36, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  /**
   * 内容类型（ARTICLE, COMMENT, USER_PROFILE, FILE）
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "content_type", nullable = false, length = 20)
  private ContentType contentType;

  /**
   * 内容ID（关联的文章、评论等ID）
   */
  @Column(name = "content_id", nullable = false, length = 36)
  private String contentId;

  /**
   * 审核状态
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private AuditStatus status = AuditStatus.PENDING;

  /**
   * 审核原因
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "reason", length = 20)
  private AuditReason reason;

  /**
   * 提交者ID（内容创建者）
   */
  @Column(name = "submitter_id", nullable = false, length = 36)
  private String submitterId;

  /**
   * 审核员ID（审核人）
   */
  @Column(name = "auditor_id", length = 36)
  private String auditorId;

  /**
   * 审核备注
   */
  @Column(name = "comment", columnDefinition = "TEXT")
  private String comment;

  /**
   * 内容快照（存储审核时的内容，用于历史追溯）
   */
  @Column(name = "content_snapshot", columnDefinition = "TEXT")
  private String contentSnapshot;

  /**
   * 敏感词列表（如果触发了敏感词检测）
   */
  @Column(name = "sensitive_words", length = 500)
  private String sensitiveWords;

  /**
   * 是否自动审核
   */
  @Column(name = "is_auto_audit", nullable = false)
  private Boolean isAutoAudit = false;

  /**
   * 审核时间
   */
  @Column(name = "audited_at")
  private LocalDateTime auditedAt;

  @Column(name = "created_at", updatable = false, nullable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
