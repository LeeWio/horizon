package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleDetailVO;
import com.sunrizon.horizon.vo.ArticleVO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IArticleService {

  /**
   * Create a new article.
   *
   * @param request DTO with article creation info
   * @return ResultResponse containing the created ArticleVO
   */
  ResultResponse<ArticleVO> createArticle(CreateArticleRequest request);

  /**
   * Update an existing article.
   *
   * @param aid     Article ID
   * @param request DTO with article update info
   * @return ResultResponse containing success or error message
   */
  ResultResponse<String> updateArticle(String aid, UpdateArticleRequest request);

  /**
   * Delete an article by ID.
   *
   * @param aid Article ID
   * @return ResultResponse containing success or error message
   */
  ResultResponse<String> deleteArticle(String aid);

  /**
   * Get a paginated list of articles.
   *
   * @param pageable Pagination and sorting info
   * @return ResultResponse containing a paginated list of ArticleVO
   */
  ResultResponse<Page<ArticleVO>> getArticles(Pageable pageable);

  /**
   * Get all articles (non-paginated).
   *
   * @return ResultResponse containing a list of all ArticleVO
   */
  ResultResponse<List<ArticleVO>> getAllArticles();

  /**
   * Get articles by a list of IDs.
   *
   * @param ids List of article IDs to find
   * @return ResultResponse containing a list of ArticleVO matching the IDs
   */
  ResultResponse<List<ArticleVO>> getArticlesByIds(List<String> ids);

  /**
   * Get an article by its ID.
   *
   * @param id The article ID to find
   * @return ResultResponse containing the ArticleVO matching the ID
   */
  ResultResponse<ArticleVO> getArticleById(String id);

  /**
   * Get article detail with SEO metadata by its ID.
   *
   * @param id The article ID to find
   * @return ResultResponse containing the ArticleDetailVO with SEO metadata
   */
  ResultResponse<ArticleDetailVO> getArticleDetailWithSeo(String id);

  /**
   * Get trending articles by view count.
   *
   * @param timeRange Time range filter (DAY, WEEK, MONTH, ALL)
   * @param pageable  Pagination info
   * @return ResultResponse containing a page of trending ArticleVO
   */
  ResultResponse<Page<ArticleVO>> getTrendingByViews(String timeRange, Pageable pageable);

  /**
   * Get trending articles by like count.
   *
   * @param timeRange Time range filter (DAY, WEEK, MONTH, ALL)
   * @param pageable  Pagination info
   * @return ResultResponse containing a page of trending ArticleVO
   */
  ResultResponse<Page<ArticleVO>> getTrendingByLikes(String timeRange, Pageable pageable);

  /**
   * Get trending articles by favorite count.
   *
   * @param timeRange Time range filter (DAY, WEEK, MONTH, ALL)
   * @param pageable  Pagination info
   * @return ResultResponse containing a page of trending ArticleVO
   */
  ResultResponse<Page<ArticleVO>> getTrendingByFavorites(String timeRange, Pageable pageable);

}
