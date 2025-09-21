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
 * 标签实体类
 * 用于文章标签管理
 */
@Entity
@Table(name = "tag")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Schema(name = "Tag", description = "标签实体")
public class Tag implements Serializable {

  private static final long serialVersionUID = -6249791470254664723L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "tid", nullable = false, length = 36, updatable = false)
  private String tid;

  @Column(name = "name", nullable = false, length = 50, unique = true)
  private String name;

  @Column(name = "slug", length = 100, unique = true)
  private String slug;

  @Column(name = "description", length = 300)
  private String description;

  @Column(name = "color", length = 7)
  private String color; // 十六进制颜色代码

  @Column(name = "article_count", nullable = false)
  private Long articleCount = 0L;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 关联关系
  @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ArticleTag> articleTags;
}
