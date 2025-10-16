package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.service.ISearchService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Search controller.
 * Provides article search, search history, and hot keywords APIs.
 *
 * @author Horizon Team
 * @since 2025-10-16
 */
@RestController
@RequestMapping("/api/search")
@Slf4j
@Tag(name = "Search", description = "Article search APIs")
public class SearchController {

  @Resource
  private ISearchService searchService;

  /**
   * Search articles by keyword.
   *
   * @param keyword Search keyword
   * @param page    Page number (default: 0)
   * @param size    Page size (default: 20)
   * @return Page of article VOs
   */
  @GetMapping
  @Operation(summary = "Search articles", description = "Search articles by keyword in title and content")
  public ResultResponse<Page<ArticleVO>> searchArticles(
      @Parameter(description = "Search keyword", required = true)
      @RequestParam String keyword,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size")
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return searchService.searchArticles(keyword, pageable);
  }

  /**
   * Advanced search with filters.
   *
   * @param keyword    Search keyword
   * @param authorId   Author ID filter (optional)
   * @param categoryId Category ID filter (optional)
   * @param tagId      Tag ID filter (optional)
   * @param page       Page number (default: 0)
   * @param size       Page size (default: 20)
   * @return Page of article VOs
   */
  @GetMapping("/advanced")
  @Operation(summary = "Advanced search", description = "Search articles with filters (author, category, tag)")
  public ResultResponse<Page<ArticleVO>> advancedSearch(
      @Parameter(description = "Search keyword", required = true)
      @RequestParam String keyword,
      @Parameter(description = "Author ID filter")
      @RequestParam(required = false) String authorId,
      @Parameter(description = "Category ID filter")
      @RequestParam(required = false) String categoryId,
      @Parameter(description = "Tag ID filter")
      @RequestParam(required = false) String tagId,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size")
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return searchService.advancedSearch(keyword, authorId, categoryId, tagId, pageable);
  }

  /**
   * Get user's search history.
   *
   * @param page Page number (default: 0)
   * @param size Page size (default: 20)
   * @return Page of search history
   */
  @GetMapping("/history")
  @Operation(summary = "Get search history", description = "Get current user's search history")
  public ResultResponse<Page<Map<String, Object>>> getSearchHistory(
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size")
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return searchService.getSearchHistory(pageable);
  }

  /**
   * Get hot search keywords.
   *
   * @param days  Days to look back (default: 7)
   * @param limit Max results (default: 10)
   * @return List of hot keywords with counts
   */
  @GetMapping("/hot")
  @Operation(summary = "Get hot keywords", description = "Get hot search keywords within specified days")
  public ResultResponse<List<Map<String, Object>>> getHotKeywords(
      @Parameter(description = "Days to look back (1=today, 7=last week, 30=last month)")
      @RequestParam(defaultValue = "7") int days,
      @Parameter(description = "Max results")
      @RequestParam(defaultValue = "10") int limit) {

    return searchService.getHotKeywords(days, limit);
  }

  /**
   * Get search suggestions by keyword prefix.
   *
   * @param prefix Keyword prefix
   * @param limit  Max results (default: 5)
   * @return List of keyword suggestions
   */
  @GetMapping("/suggestions")
  @Operation(summary = "Get search suggestions", description = "Get keyword suggestions by prefix (autocomplete)")
  public ResultResponse<List<String>> getSearchSuggestions(
      @Parameter(description = "Keyword prefix", required = true)
      @RequestParam String prefix,
      @Parameter(description = "Max results")
      @RequestParam(defaultValue = "5") int limit) {

    return searchService.getSearchSuggestions(prefix, limit);
  }

  /**
   * Clear user's search history.
   *
   * @return Success message
   */
  @DeleteMapping("/history")
  @Operation(summary = "Clear search history", description = "Clear current user's search history")
  public ResultResponse<String> clearSearchHistory() {
    return searchService.clearSearchHistory();
  }
}
