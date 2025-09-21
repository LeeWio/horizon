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
 * 文章-标签关联实体类
 * 多对多关系的中间表
 */
@Entity
@Table(name = "article_tag", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"article_id", "tag_id"})
})
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "ArticleTag", description = "文章标签关联")
public class ArticleTag implements Serializable {

  private static final long serialVersionUID = -6249791470254664731L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "atid", nullable = false, length = 36, updatable = false)
  @Schema(description = "关联ID")
  private String atid;

  @Column(name = "article_id", nullable = false, length = 36)
  @Schema(description = "文章ID")
  private String articleId;

  @Column(name = "tag_id", nullable = false, length = 36)
  @Schema(description = "标签ID")
  private String tagId;

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
  @JoinColumn(name = "tag_id", insertable = false, updatable = false)
  private Tag tag;
}
