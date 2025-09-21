package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.LikeType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 点赞实体类
 * 支持文章和评论的点赞功能
 */
@Entity
@Table(name = "likes", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"user_id", "target_id", "target_type"})
})
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Like implements Serializable {

  private static final long serialVersionUID = -6249791470254664725L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "lid", nullable = false, length = 36, updatable = false)
  private String lid;

  @Column(name = "uid", nullable = false, length = 36)
  private String userId;

  @Column(name = "target_id", nullable = false, length = 36)
  private String targetId; // 被点赞的对象ID（文章ID或评论ID）

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false, length = 20)
  private LikeType targetType; // 点赞对象类型

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // 关联关系
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uid", insertable = false, updatable = false)
  private User user;
}
