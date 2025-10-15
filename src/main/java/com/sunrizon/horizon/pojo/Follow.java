package com.sunrizon.horizon.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Follow Entity
 * 用户关注关系实体类
 */
@Entity
@Table(name = "follow",
    uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Follow implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, length = 36, updatable = false)
  private String id;

  /**
   * 关注者ID（谁关注了）
   */
  @Column(name = "follower_id", nullable = false, length = 36)
  private String followerId;

  /**
   * 被关注者ID（被谁关注）
   */
  @Column(name = "following_id", nullable = false, length = 36)
  private String followingId;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
