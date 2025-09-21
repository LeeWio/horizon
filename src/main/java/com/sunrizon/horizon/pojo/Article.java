package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.ArticleStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 文章实体类
 * 代表博客系统中的文章内容
 */
@Table(name = "article")
@Entity
@Setter
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "Article", description = "文章实体")
public class Article implements Serializable {

  private static final long serialVersionUID = -6249794470754667710L;

  @Id
  @Column(name = "aid", nullable = false, length = 36, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  @Schema(description = "文章ID")
  private String aid;

  @Column(name = "title", nullable = false, length = 200)
  @Schema(description = "文章标题", example = "Spring Boot 最佳实践")
  private String title;

  @Column(name = "slug", length = 200, unique = true)
  @Schema(description = "URL别名", example = "spring-boot-best-practices")
  private String slug;

  @Column(name = "summary", length = 1000)
  @Schema(description = "文章摘要", example = "本文介绍了Spring Boot开发中的最佳实践...")
  private String summary;

  @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
  @Schema(description = "文章内容")
  private String content;

  @Column(name = "cover_image", length = 500)
  @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
  private String coverImage;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Schema(description = "文章状态", example = "PUBLISHED")
  private ArticleStatus status = ArticleStatus.DRAFT;

  @Column(name = "author_id", nullable = false, length = 36, updatable = false)
  @Schema(description = "作者ID")
  private String authorId;

  @Column(name = "is_featured", nullable = false)
  @Schema(description = "是否精选", example = "false")
  private Boolean isFeatured = false;

  @Column(name = "is_top", nullable = false)
  @Schema(description = "是否置顶", example = "false")
  private Boolean isTop = false;

  @Column(name = "published_at")
  @Schema(description = "发布时间")
  private LocalDateTime publishedAt;

  @Column(name = "created_at", updatable = false, nullable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false, nullable = false)
  private String createdBy;

  @LastModifiedBy
  @Column(name = "updated_by", nullable = false)
  private String updatedBy;

  // 关联关系
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", insertable = false, updatable = false)
  private User author;

  @OneToOne(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private ArticleStats stats;

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ArticleCategory> articleCategories;

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ArticleTag> articleTags;

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Comment> comments;
}
