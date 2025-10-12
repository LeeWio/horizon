package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.utils.ResultResponse;
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

}
