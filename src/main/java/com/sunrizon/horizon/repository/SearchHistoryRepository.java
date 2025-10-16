package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Search history repository.
 *
 * @author Horizon Team
 * @since 2025-10-16
 */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, String> {

  /**
   * Find search history by user ID with pagination.
   *
   * @param userId   User ID
   * @param pageable Pagination parameters
   * @return Page of search history
   */
  Page<SearchHistory> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

  /**
   * Get hot search keywords within time range.
   * Returns top keywords ordered by frequency.
   *
   * @param startTime Start time
   * @param limit     Max results
   * @return List of hot keywords
   */
  @Query("SELECT s.keyword, COUNT(s.keyword) as count " +
      "FROM SearchHistory s " +
      "WHERE s.createdAt >= :startTime " +
      "GROUP BY s.keyword " +
      "ORDER BY count DESC")
  List<Object[]> findHotKeywords(@Param("startTime") LocalDateTime startTime, Pageable pageable);

  /**
   * Get search suggestions by keyword prefix.
   *
   * @param prefix Keyword prefix
   * @param limit  Max results
   * @return List of keyword suggestions
   */
  @Query("SELECT DISTINCT s.keyword " +
      "FROM SearchHistory s " +
      "WHERE s.keyword LIKE :prefix " +
      "ORDER BY s.keyword DESC")
  List<String> findSuggestionsByPrefix(@Param("prefix") String prefix, Pageable pageable);

  /**
   * Delete old search history records.
   *
   * @param before Delete records before this time
   * @return Number of deleted records
   */
  Long deleteByCreatedAtBefore(LocalDateTime before);

  /**
   * Delete search history by user ID.
   *
   * @param userId User ID
   */
  void deleteByUserId(String userId);
}
