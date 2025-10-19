package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Table(name = "tag", indexes = {
    @Index(name = "idx_tag_name", columnList = "name"),
    @Index(name = "idx_tag_slug", columnList = "slug"),
    @Index(name = "idx_tag_created_at", columnList = "created_at")
})
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

  @NotBlank(message = "Tag name cannot be blank")
  @Size(max = 50, message = "Tag name must not exceed 50 characters")
  @Column(name = "name", nullable = false, length = 50, unique = true)
  private String name;

  @Size(max = 100, message = "Tag slug must not exceed 100 characters")
  @Column(name = "slug", length = 100, unique = true)
  private String slug;

  @NotBlank(message = "Tag icon cannot be blank")
  @Column(name = "icon", nullable = false)
  private String icon = "Java";

  @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
  @Column(name = "color", length = 7)
  private String color = "#6c757d";

  @Size(max = 200, message = "Tag description must not exceed 200 characters")
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
