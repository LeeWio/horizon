package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分类实体类
 * 用于文章分类管理
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "Category", description = "分类实体")
public class Category implements Serializable {

  private static final long serialVersionUID = -6249791470254664722L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "cid", nullable = false, length = 36, updatable = false)
  private String cid;

  @Column(name = "name", nullable = false, length = 100, unique = true)
  private String name;

  @Column(name = "slug", length = 150, unique = true)
  private String slug;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "color", length = 7)
  private String color; // 十六进制颜色代码，如 #FF5722

  @Column(name = "icon", length = 100)
  private String icon; // 图标类名或路径

  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 0;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "article_count", nullable = false)
  private Long articleCount = 0L;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 关联关系
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ArticleCategory> articleCategories;

  // 自关联：支持分类层级
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Category> children;
}
