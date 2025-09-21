package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户统计实体类
 * 存储用户的统计数据
 */
@Entity
@Table(name = "user_stats")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "UserStats", description = "用户统计数据")
public class UserStats implements Serializable {

  private static final long serialVersionUID = -6249791470254664729L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "usid", nullable = false, length = 36, updatable = false)
  @Schema(description = "统计ID")
  private String usid;

  @Column(name = "uid", nullable = false, length = 36, unique = true)
  @Schema(description = "用户ID")
  private String userId;

  @Column(name = "article_count", nullable = false)
  @Schema(description = "文章数量", example = "10")
  private Long articleCount = 0L;

  @Column(name = "comment_count", nullable = false)
  @Schema(description = "评论数量", example = "50")
  private Long commentCount = 0L;

  @Column(name = "like_received_count", nullable = false)
  @Schema(description = "收到点赞数", example = "200")
  private Long likeReceivedCount = 0L;

  @Column(name = "like_given_count", nullable = false)
  @Schema(description = "给出点赞数", example = "150")
  private Long likeGivenCount = 0L;

  @Column(name = "followers_count", nullable = false)
  @Schema(description = "粉丝数量", example = "100")
  private Long followersCount = 0L;

  @Column(name = "following_count", nullable = false)
  @Schema(description = "关注数量", example = "50")
  private Long followingCount = 0L;

  @Column(name = "last_active_at")
  @Schema(description = "最后活跃时间")
  private LocalDateTime lastActiveAt;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  @Schema(description = "创建时间")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Schema(description = "更新时间")
  private LocalDateTime updatedAt;

  // 关联关系
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uid", insertable = false, updatable = false)
  private User user;
}
