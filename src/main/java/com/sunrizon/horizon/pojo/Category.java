package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "category")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Category implements Serializable {

  private static final long serialVersionUID = -2249794470754667710L;

  @Id
  @Column(name = "id", nullable = false, length = 36)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String cid;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "slug", length = 100, unique = true)
  private String slug;

  @Column(name = "description", length = 200)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", referencedColumnName = "id")
  private Category parent;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /** Articles associated with this category */
  @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
  private Set<Article> articles;

  public boolean isRootCategory() {
    return this.parent == null;
  }

  public String getFullCategoryPath() {
    if (isRootCategory()) {
      return name;
    }
    return parent.getFullCategoryPath() + " / " + name;
  }

}
