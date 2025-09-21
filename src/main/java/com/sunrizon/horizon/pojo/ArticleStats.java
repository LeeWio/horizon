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
 * 文章统计实体类
 * 存储文章的统计数据，避免Article表过于臃肿
 */
@Entity
@Table(name = "article_stats")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "ArticleStats", description = "文章统计数据")
public class ArticleStats implements Serializable {

  private static final long serialVersionUID = -6249791470254664728L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "asid", nullable = false, length = 36, updatable = false)
  @Schema(description = "统计ID")
  private String asid;

  @Column(name = "article_id", nullable = false, length = 36, unique = true)
  @Schema(description = "文章ID")
  private String articleId;

  @Column(name = "view_count", nullable = false)
  @Schema(description = "浏览次数", example = "1000")
  private Long viewCount = 0L;

  @Column(name = "like_count", nullable = false)
  @Schema(description = "点赞数", example = "50")
  private Long likeCount = 0L;

  @Column(name = "comment_count", nullable = false)
  @Schema(description = "评论数", example = "25")
  private Long commentCount = 0L;

  @Column(name = "share_count", nullable = false)
  @Schema(description = "分享数", example = "10")
  private Long shareCount = 0L;

  @Column(name = "favorite_count", nullable = false)
  @Schema(description = "收藏数", example = "30")
  private Long favoriteCount = 0L;

  @Column(name = "last_viewed_at")
  @Schema(description = "最后浏览时间")
  private LocalDateTime lastViewedAt;

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
  @JoinColumn(name = "article_id", insertable = false, updatable = false)
  private Article article;
}
