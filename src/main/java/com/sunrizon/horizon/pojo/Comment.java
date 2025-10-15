package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.sunrizon.horizon.enums.CommentStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "comment", indexes = {
    @Index(name = "idx_comment_article_id", columnList = "article_id"),
    @Index(name = "idx_comment_user_id", columnList = "user_id"),
    @Index(name = "idx_comment_parent_id", columnList = "parent_id"),
    @Index(name = "idx_comment_created_at", columnList = "created_at")
})
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Comment implements Serializable {

  private static final long serialVersionUID = -6249794470754667713L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "cid", nullable = false, length = 36, updatable = false)
  private String cid;

  @Column(name = "article_id", nullable = false, length = 36)
  private String articleId;

  @Column(name = "user_id", nullable = false, length = 36)
  private String userId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "parent_id", length = 36)
  private String parentId;

  @Column(name = "likes_count", nullable = false)
  private Integer likesCount = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private CommentStatus status = CommentStatus.APPROVED;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
