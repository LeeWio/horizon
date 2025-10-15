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

@Table(name = "tag")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Tag implements Serializable {

  private static final long serialVersionUID = -6249794470754667712L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "tid", nullable = false, length = 36, updatable = false)
  private String tid;

  @Column(name = "name", nullable = false, length = 50, unique = true)
  private String name;

  @Column(name = "slug", length = 100, unique = true)
  private String slug;

  @Column(name = "icon", nullable = false)
  private String icon;

  @Column(name = "description", length = 200)
  private String description;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /** Articles associated with this tag */
  @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
  private Set<Article> articles;
}
