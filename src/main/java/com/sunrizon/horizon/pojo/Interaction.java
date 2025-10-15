package com.sunrizon.horizon.pojo;

import com.sunrizon.horizon.enums.InteractionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Interaction Entity
 * 通用互动实体类（支持文章和评论的点赞、收藏、分享）
 */
@Entity
@Table(name = "interaction",
    uniqueConstraints = @UniqueConstraint(columnNames = {"target_id", "target_type", "user_id", "type"}),
    indexes = {
        @Index(name = "idx_interaction_target", columnList = "target_id, target_type"),
        @Index(name = "idx_interaction_user_id", columnList = "user_id"),
        @Index(name = "idx_interaction_type", columnList = "type"),
        @Index(name = "idx_interaction_user_type", columnList = "user_id, type")
    })
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Interaction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, length = 36, updatable = false)
  private String id;

  /**
   * 目标ID（可以是文章ID或评论ID）
   */
  @Column(name = "target_id", nullable = false, length = 36)
  private String targetId;

  /**
   * 目标类型（ARTICLE或COMMENT）
   */
  @Column(name = "target_type", nullable = false, length = 20)
  private String targetType;

  /**
   * 兼容字段：文章ID（向后兼容，逐步迁移后可删除）
   */
  @Column(name = "article_id", length = 36)
  private String articleId;

  @Column(name = "user_id", nullable = false, length = 36)
  private String userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private InteractionType type;

  @Column(name = "metadata", length = 500)
  private String metadata;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
