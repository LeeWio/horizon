package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sunrizon.horizon.enums.SeriesStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "series")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Series implements Serializable {

  private static final long serialVersionUID = -3249794470754667710L;

  @Id
  @Column(name = "sid", nullable = false, length = 36)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String sid;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "slug", length = 150, unique = true)
  private String slug;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "cover_image")
  private String coverImage;

  @Column(name = "status", length = 20)
  @Enumerated(EnumType.STRING)
  private SeriesStatus status = SeriesStatus.DRAFT; // DRAFT, PUBLISHED, ARCHIVED

  @Column(name = "author_id", nullable = false, length = 36)
  private String authorId;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /** Articles in this series */
  @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "series_article", joinColumns = @JoinColumn(name = "series_id", referencedColumnName = "sid"), inverseJoinColumns = @JoinColumn(name = "aid", referencedColumnName = "aid"))
  private Set<Article> articles;

  public boolean isPublished() {
    return SeriesStatus.PUBLISHED.equals(this.status);
  }
}
