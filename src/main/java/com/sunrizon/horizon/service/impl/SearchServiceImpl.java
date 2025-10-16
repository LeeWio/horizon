package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.*;
import com.sunrizon.horizon.repository.*;
import com.sunrizon.horizon.service.ISearchService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.ArticleVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Search service implementation.
 * Implements article search, search history, and hot keywords functionality.
 *
 * @author Horizon Team
 * @since 2025-10-16
 */
@Service
@Slf4j
public class SearchServiceImpl implements ISearchService {

  @Resource
  private ArticleRepository articleRepository;

  @Resource
  private SearchHistoryRepository searchHistoryRepository;

  @Resource
  private CategoryRepository categoryRepository;

  @Resource
  private TagRepository tagRepository;

  @Resource
  private UserRepository userRepository;

  @Resource
  private SecurityContextUtil securityContextUtil;

  /**
   * Search articles by keyword.
   */
  @Override
  @Transactional
  public ResultResponse<Page<ArticleVO>> searchArticles(String keyword, Pageable pageable) {
    // Validate keyword
    if (StrUtil.isBlank(keyword)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Search keyword cannot be empty");
    }

    // Trim keyword
    String trimmedKeyword = keyword.trim();

    // Search articles (only PUBLISHED)
    Page<Article> articlePage = articleRepository.searchByKeyword(
        trimmedKeyword,
        ArticleStatus.PUBLISHED,
        pageable
    );

    // Record search history
    recordSearchHistory(trimmedKeyword, articlePage.getTotalElements());

    // Convert to VO
    Page<ArticleVO> voPage = articlePage.map(article -> 
        BeanUtil.copyProperties(article, ArticleVO.class)
    );

    log.info("Search completed: keyword='{}', results={}", trimmedKeyword, voPage.getTotalElements());

    return ResultResponse.success(voPage);
  }

  /**
   * Advanced search with filters.
   */
  @Override
  @Transactional
  public ResultResponse<Page<ArticleVO>> advancedSearch(
      String keyword,
      String authorId,
      String categoryId,
      String tagId,
      Pageable pageable) {

    // Validate keyword
    if (StrUtil.isBlank(keyword)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Search keyword cannot be empty");
    }

    String trimmedKeyword = keyword.trim();
    Page<Article> articlePage;

    // Advanced search logic
    if (StrUtil.isNotBlank(authorId)) {
      // Search by author
      articlePage = articleRepository.searchByKeywordAndAuthor(
          trimmedKeyword,
          authorId,
          ArticleStatus.PUBLISHED,
          pageable
      );
    } else if (StrUtil.isNotBlank(categoryId)) {
      // Search by category
      Category category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found"));
      articlePage = articleRepository.searchByKeywordAndCategory(
          trimmedKeyword,
          category,
          ArticleStatus.PUBLISHED,
          pageable
      );
    } else if (StrUtil.isNotBlank(tagId)) {
      // Search by tag
      Tag tag = tagRepository.findById(tagId)
          .orElseThrow(() -> new RuntimeException("Tag not found"));
      articlePage = articleRepository.searchByKeywordAndTag(
          trimmedKeyword,
          tag,
          ArticleStatus.PUBLISHED,
          pageable
      );
    } else {
      // Default: basic search
      articlePage = articleRepository.searchByKeyword(
          trimmedKeyword,
          ArticleStatus.PUBLISHED,
          pageable
      );
    }

    // Record search history
    recordSearchHistory(trimmedKeyword, articlePage.getTotalElements());

    // Convert to VO
    Page<ArticleVO> voPage = articlePage.map(article -> 
        BeanUtil.copyProperties(article, ArticleVO.class)
    );

    log.info("Advanced search completed: keyword='{}', author='{}', category='{}', tag='{}', results={}",
        trimmedKeyword, authorId, categoryId, tagId, voPage.getTotalElements());

    return ResultResponse.success(voPage);
  }

  /**
   * Get user's search history.
   */
  @Override
  public ResultResponse<Page<Map<String, Object>>> getSearchHistory(Pageable pageable) {
    // Get current user ID
    Optional<String> currentUserIdOpt = securityContextUtil.getCurrentUserId();
    if (currentUserIdOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.UNAUTHORIZED);
    }

    String userId = currentUserIdOpt.get();

    // Query search history
    Page<SearchHistory> historyPage = searchHistoryRepository
        .findByUserIdOrderByCreatedAtDesc(userId, pageable);

    // Convert to Map
    Page<Map<String, Object>> voPage = historyPage.map(history -> {
      Map<String, Object> map = new HashMap<>();
      map.put("keyword", history.getKeyword());
      map.put("resultCount", history.getResultCount());
      map.put("createdAt", history.getCreatedAt());
      return map;
    });

    return ResultResponse.success(voPage);
  }

  /**
   * Get hot search keywords.
   */
  @Override
  public ResultResponse<List<Map<String, Object>>> getHotKeywords(int days, int limit) {
    // Calculate start time
    LocalDateTime startTime = LocalDateTime.now().minusDays(days);

    // Query hot keywords
    Pageable pageable = PageRequest.of(0, limit);
    List<Object[]> results = searchHistoryRepository.findHotKeywords(startTime, pageable);

    // Convert to Map
    List<Map<String, Object>> hotKeywords = results.stream()
        .map(row -> {
          Map<String, Object> map = new HashMap<>();
          map.put("keyword", row[0]);
          map.put("count", row[1]);
          return map;
        })
        .collect(Collectors.toList());

    log.info("Hot keywords retrieved: days={}, limit={}, results={}", days, limit, hotKeywords.size());

    return ResultResponse.success(hotKeywords);
  }

  /**
   * Get search suggestions by keyword prefix.
   */
  @Override
  public ResultResponse<List<String>> getSearchSuggestions(String prefix, int limit) {
    // Validate prefix
    if (StrUtil.isBlank(prefix)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Prefix cannot be empty");
    }

    // Query suggestions
    String pattern = prefix.trim() + "%";
    Pageable pageable = PageRequest.of(0, limit);
    List<String> suggestions = searchHistoryRepository.findSuggestionsByPrefix(pattern, pageable);

    return ResultResponse.success(suggestions);
  }

  /**
   * Clear user's search history.
   */
  @Override
  @Transactional
  public ResultResponse<String> clearSearchHistory() {
    // Get current user ID
    Optional<String> currentUserIdOpt = securityContextUtil.getCurrentUserId();
    if (currentUserIdOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.UNAUTHORIZED);
    }

    String userId = currentUserIdOpt.get();

    // Delete search history
    searchHistoryRepository.deleteByUserId(userId);

    log.info("Search history cleared for user: {}", userId);

    return ResultResponse.success("Search history cleared successfully");
  }

  /**
   * Record search history.
   *
   * @param keyword     Search keyword
   * @param resultCount Number of results
   */
  private void recordSearchHistory(String keyword, long resultCount) {
    try {
      // Get current user ID (optional for anonymous users)
      Optional<String> userIdOpt = securityContextUtil.getCurrentUserId();

      // Create search history record
      SearchHistory history = new SearchHistory();
      history.setKeyword(keyword);
      history.setResultCount((int) resultCount);
      userIdOpt.ifPresent(history::setUserId);

      // Save
      searchHistoryRepository.save(history);

      log.debug("Search history recorded: keyword='{}', userId='{}', results={}",
          keyword, userIdOpt.orElse("anonymous"), resultCount);

    } catch (Exception e) {
      // Search history failure should not affect search functionality
      log.warn("Failed to record search history: {}", e.getMessage());
    }
  }
}
