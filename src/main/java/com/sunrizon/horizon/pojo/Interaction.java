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
 * Article Interaction Entity
 * 文章互动实体类（点赞、收藏、分享）
 */
@Entity
@Table(name = "interaction",
    uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "user_id", "type"}),
    indexes = {
        @Index(name = "idx_interaction_article_id", columnList = "article_id"),
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

  @Column(name = "article_id", nullable = false, length = 36)
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
