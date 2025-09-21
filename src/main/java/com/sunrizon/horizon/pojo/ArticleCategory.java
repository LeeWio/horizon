package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 文章-分类关联实体类
 * 多对多关系的中间表
 */
@Entity
@Table(name = "article_category", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"article_id", "category_id"})
})
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "ArticleCategory", description = "文章分类关联")
public class ArticleCategory implements Serializable {

  private static final long serialVersionUID = -6249791470254664730L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "acid", nullable = false, length = 36, updatable = false)
  @Schema(description = "关联ID")
  private String acid;

  @Column(name = "article_id", nullable = false, length = 36)
  @Schema(description = "文章ID")
  private String articleId;

  @Column(name = "category_id", nullable = false, length = 36)
  @Schema(description = "分类ID")
  private String categoryId;

  @Column(name = "sort_order", nullable = false)
  @Schema(description = "排序", example = "1")
  private Integer sortOrder = 0;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  @Schema(description = "创建时间")
  private LocalDateTime createdAt;

  // 关联关系
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id", insertable = false, updatable = false)
  private Article article;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", insertable = false, updatable = false)
  private Category category;
}
