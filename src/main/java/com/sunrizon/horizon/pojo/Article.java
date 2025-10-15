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

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "article")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Article implements Serializable {

  private static final long serialVersionUID = -6249794470754667710L;

  @Id
  @Column(name = "aid", nullable = false, length = 36, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String aid;

  @Column(name = "title", nullable = false, length = 100, unique = true)
  private String title;

  @Column(name = "slug", length = 150, unique = true)
  private String slug;

  @Column(name = "summary", length = 500)
  private String summary;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "cover_image")
  private String coverImage;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private ArticleStatus status = ArticleStatus.DRAFT;

  @Column(name = "author_id", nullable = false, length = 36, updatable = false)
  private String authorId;

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

  @Column(name = "is_featured")
  private Boolean isFeatured = false;

  @Column(name = "view_count", nullable = false)
  private Long viewCount = 0L;

  @Column(name = "like_count", nullable = false)
  private Long likeCount = 0L;

  @Column(name = "favorite_count", nullable = false)
  private Long favoriteCount = 0L;

  @Column(name = "share_count", nullable = false)
  private Long shareCount = 0L;

  /** Categories associated with this article */
  @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "article_category", joinColumns = @JoinColumn(name = "aid", referencedColumnName = "aid"), inverseJoinColumns = @JoinColumn(name = "cid", referencedColumnName = "id"))
  private Set<Category> categories;

  /** Series this article belongs to */
  @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "series_article", joinColumns = @JoinColumn(name = "aid", referencedColumnName = "aid"), inverseJoinColumns = @JoinColumn(name = "series_id", referencedColumnName = "sid"))
  private Set<Series> series;

  /** Tags associated with this article */
  @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "article_tag", joinColumns = @JoinColumn(name = "aid", referencedColumnName = "aid"), inverseJoinColumns = @JoinColumn(name = "tid", referencedColumnName = "tid"))
  private Set<Tag> tags;
}
