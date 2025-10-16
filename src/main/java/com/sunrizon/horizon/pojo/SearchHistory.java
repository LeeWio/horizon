package com.sunrizon.horizon.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Search history entity.
 * Records user search keywords for analytics and suggestions.
 *
 * @author Horizon Team
 * @since 2025-10-16
 */
@Table(name = "search_history", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_keyword", columnList = "keyword"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class SearchHistory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, length = 36, updatable = false)
  private String id;

  /**
   * User ID who performed the search.
   * Nullable for anonymous users.
   */
  @Column(name = "user_id", length = 36)
  private String userId;

  /**
   * Search keyword.
   */
  @Column(name = "keyword", nullable = false, length = 200)
  private String keyword;

  /**
   * Number of search results returned.
   */
  @Column(name = "result_count")
  private Integer resultCount;

  /**
   * Search timestamp.
   */
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
