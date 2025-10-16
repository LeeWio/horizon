package com.sunrizon.horizon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.service.IArticleService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleDetailVO;
import com.sunrizon.horizon.vo.ArticleVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/article")
@Tag(name = "Article Management", description = "Article management APIs")
@Slf4j
public class ArticleController {

  @Resource
  private IArticleService articleService;

  /**
   * Create a new article.
   *
   * @param request DTO containing article creation data
   * @return ResultResponse wrapping the created ArticleVO
   */
  @PostMapping("/create")
  public ResultResponse<ArticleVO> createArticle(@Valid @RequestBody CreateArticleRequest request) {
    return articleService.createArticle(request);
  }

  /**
   * Update an existing article.
   *
   * @param id      Article ID
   * @param request DTO containing article update data
   * @return ResultResponse with success or error message
   */
  @PutMapping("/{id}")
  public ResultResponse<String> updateArticle(
      @PathVariable("id") String id,
      @Valid @RequestBody UpdateArticleRequest request) {
    return articleService.updateArticle(id, request);
  }

  /**
   * Delete an article by ID.
   *
   * @param id Article ID
   * @return ResultResponse with success or error message
   */
  @DeleteMapping("/{id}")
  public ResultResponse<String> deleteArticle(@PathVariable("id") String id) {
    return articleService.deleteArticle(id);
  }

  /**
   * Retrieve all articles (non-paginated).
   * 
   * @return ResultResponse wrapping list of all ArticleVO
   */
  @GetMapping("/all")
  public ResultResponse<List<ArticleVO>> getAllArticles() {
    return articleService.getAllArticles();
  }

  /**
   * Retrieve articles by a list of IDs.
   * 
   * @param ids List of article IDs to find
   * @return ResultResponse wrapping list of ArticleVO matching the IDs
   */
  @PostMapping("/by-ids")
  public ResultResponse<List<ArticleVO>> getArticlesByIds(@RequestBody List<String> ids) {
    return articleService.getArticlesByIds(ids);
  }

  /**
   * Retrieve an article by its ID.
   * 
   * @param id The article ID to find
   * @return ResultResponse wrapping ArticleVO matching the ID
   */
  @GetMapping("/{id}")
  public ResultResponse<ArticleVO> getArticleById(@PathVariable("id") String id) {
    return articleService.getArticleById(id);
  }

  /**
   * Retrieve article detail with SEO metadata.
   * 
   * @param id The article ID to find
   * @return ResultResponse wrapping ArticleDetailVO with SEO metadata
   */
  @GetMapping("/{id}/detail")
  @Operation(summary = "Get article detail with SEO metadata")
  public ResultResponse<ArticleDetailVO> getArticleDetailWithSeo(@PathVariable("id") String id) {
    return articleService.getArticleDetailWithSeo(id);
  }

  @GetMapping
  @Operation(summary = "Get articles with pagination")
  public ResultResponse<Page<ArticleVO>> getArticles(Pageable pageable) {
    return articleService.getArticles(pageable);
  }

  /**
   * Get trending articles.
   *
   * @param type      Ranking type: VIEW, LIKE, FAVORITE (default: VIEW)
   * @param timeRange Time range: DAY, WEEK, MONTH, ALL (default: WEEK)
   * @param pageable  Pagination info
   * @return ResultResponse wrapping page of trending ArticleVO
   */
  @GetMapping("/trending")
  @Operation(summary = "Get trending articles")
  public ResultResponse<Page<ArticleVO>> getTrendingArticles(
      @RequestParam(defaultValue = "VIEW") String type,
      @RequestParam(defaultValue = "WEEK") String timeRange,
      Pageable pageable) {

    return switch (type.toUpperCase()) {
      case "LIKE" -> articleService.getTrendingByLikes(timeRange, pageable);
      case "FAVORITE" -> articleService.getTrendingByFavorites(timeRange, pageable);
      default -> articleService.getTrendingByViews(timeRange, pageable);
    };
  }

}
