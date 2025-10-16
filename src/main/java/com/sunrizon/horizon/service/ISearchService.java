package com.sunrizon.horizon.service;

import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Search service interface.
 * Provides article search, search history, and hot keywords functionality.
 *
 * @author Horizon Team
 * @since 2025-10-16
 */
public interface ISearchService {

  /**
   * Search articles by keyword.
   * Searches in title and content, records search history.
   *
   * @param keyword  Search keyword
   * @param pageable Pagination parameters
   * @return Page of article VOs
   */
  ResultResponse<Page<ArticleVO>> searchArticles(String keyword, Pageable pageable);

  /**
   * Advanced search with filters.
   *
   * @param keyword    Search keyword
   * @param authorId   Author ID (optional)
   * @param categoryId Category ID (optional)
   * @param tagId      Tag ID (optional)
   * @param pageable   Pagination parameters
   * @return Page of article VOs
   */
  ResultResponse<Page<ArticleVO>> advancedSearch(String keyword, String authorId, String categoryId, String tagId, Pageable pageable);

  /**
   * Get user's search history.
   *
   * @param pageable Pagination parameters
   * @return Page of search history
   */
  ResultResponse<Page<Map<String, Object>>> getSearchHistory(Pageable pageable);

  /**
   * Get hot search keywords.
   *
   * @param days Days to look back (1=today, 7=last week, 30=last month)
   * @param limit Max results
   * @return List of hot keywords with counts
   */
  ResultResponse<List<Map<String, Object>>> getHotKeywords(int days, int limit);

  /**
   * Get search suggestions by keyword prefix.
   *
   * @param prefix Keyword prefix
   * @param limit  Max results
   * @return List of keyword suggestions
   */
  ResultResponse<List<String>> getSearchSuggestions(String prefix, int limit);

  /**
   * Clear user's search history.
   *
   * @return Success message
   */
  ResultResponse<String> clearSearchHistory();
}
