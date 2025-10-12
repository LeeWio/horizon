package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.pojo.Category;
import com.sunrizon.horizon.pojo.Series;
import com.sunrizon.horizon.pojo.Tag;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.repository.CategoryRepository;
import com.sunrizon.horizon.repository.SeriesRepository;
import com.sunrizon.horizon.repository.TagRepository;
import com.sunrizon.horizon.service.IArticleService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of IArticleService.
 * <p>
 * Handles article management operations.
 */
@Service
@Slf4j
public class ArticleServiceImpl implements IArticleService {

  @Resource
  private ArticleRepository articleRepository;

  @Resource
  private CategoryRepository categoryRepository;

  @Resource
  private SeriesRepository seriesRepository;

  @Resource
  private TagRepository tagRepository;

  /**
   * Create a new article.
   *
   * Validates uniqueness, and saves article with associated categories, series,
   * and tags.
   *
   * @param request Article creation request
   * @return {@link ResultResponse} with created {@link ArticleVO}
   */
  @Override
  @Transactional
  public ResultResponse<ArticleVO> createArticle(CreateArticleRequest request) {
    // Check slug uniqueness if provided
    if (StrUtil.isNotBlank(request.getSlug()) && articleRepository.existsBySlug(request.getSlug())) {
      return ResultResponse.error(ResponseCode.ARTICLE_SLUG_EXISTS);
    }

    // Map DTO to Entity
    // Article article = new Article();
    // article.setTitle(request.getTitle());
    // article.setSlug(request.getSlug());
    // article.setSummary(request.getSummary());
    // article.setContent(request.getContent());
    // article.setCoverImage(request.getCoverImage());
    // article.setAuthorId(request.getAuthorId());

    Article article = BeanUtil.copyProperties(request, Article.class);

    // If slug is not provided, generate it from title
    if (StrUtil.isBlank(article.getSlug())) {
      article.setSlug(generateSlugFromTitle(request.getTitle()));
    }

    // Associate categories if provided
    if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
      Set<Category> categories = categoryRepository.findAllById(request.getCategoryIds())
          .stream().collect(Collectors.toSet());
      article.setCategories(categories);
    }

    // Associate series if provided
    if (request.getSeriesIds() != null && !request.getSeriesIds().isEmpty()) {
      Set<Series> seriesSet = seriesRepository.findAllById(request.getSeriesIds())
          .stream().collect(Collectors.toSet());
      article.setSeries(seriesSet);
    }

    // Associate tags if provided
    if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
      Set<Tag> tags = tagRepository.findAllById(request.getTagIds())
          .stream().collect(Collectors.toSet());
      article.setTags(tags);
    }

    // Save article
    Article savedArticle = articleRepository.save(article);

    // Convert to VO and return
    ArticleVO articleVO = BeanUtil.copyProperties(savedArticle, ArticleVO.class);
    return ResultResponse.success(ResponseCode.SUCCESS, articleVO);
  }

  /**
   * Generate a slug from a title by converting to lowercase and replacing spaces
   * with hyphens.
   *
   * @param title The original title
   * @return Generated slug
   */
  private String generateSlugFromTitle(String title) {
    if (StrUtil.isBlank(title)) {
      return "";
    }
    return title.trim().toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
  }

  /**
   * Get a paginated list of articles.
   *
   * @param pageable Pagination and sorting info
   * @return {@link ResultResponse} with paginated {@link ArticleVO} list
   */
  @Override
  public ResultResponse<org.springframework.data.domain.Page<ArticleVO>> getArticles(
      org.springframework.data.domain.Pageable pageable) {
    // Fetch paginated articles
    org.springframework.data.domain.Page<Article> articlePage = articleRepository.findAll(pageable);

    // Map entity to VO
    org.springframework.data.domain.Page<ArticleVO> voPage = articlePage
        .map(article -> BeanUtil.copyProperties(article, ArticleVO.class));

    // Return response
    return ResultResponse.success(voPage);
  }

  /**
   * Get all articles (non-paginated).
   *
   * @return {@link ResultResponse} with list of all {@link ArticleVO}
   */
  @Override
  public ResultResponse<List<ArticleVO>> getAllArticles() {
    // Fetch all articles
    List<Article> articles = articleRepository.findAll();

    // Map entity to VO list
    List<ArticleVO> voList = articles.stream()
        .map(article -> BeanUtil.copyProperties(article, ArticleVO.class))
        .collect(Collectors.toList());

    // Return response
    return ResultResponse.success(voList);
  }

  /**
   * Get articles by a list of IDs.
   *
   * @param ids List of article IDs to find
   * @return {@link ResultResponse} with list of {@link ArticleVO} matching the
   *         IDs
   */
  @Override
  public ResultResponse<List<ArticleVO>> getArticlesByIds(List<String> ids) {
    // Fetch articles by IDs
    List<Article> articles = articleRepository.findAllById(ids);

    // Map entity to VO list
    List<ArticleVO> voList = articles.stream()
        .map(article -> BeanUtil.copyProperties(article, ArticleVO.class))
        .collect(Collectors.toList());

    // Return response
    return ResultResponse.success(voList);
  }

  /**
   * Get an article by its ID.
   *
   * @param id The article ID to find
   * @return {@link ResultResponse} with {@link ArticleVO} matching the ID
   */
  @Override
  public ResultResponse<ArticleVO> getArticleById(String id) {
    // Fetch article by ID
    Article article = articleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

    // Map entity to VO
    ArticleVO articleVO = BeanUtil.copyProperties(article, ArticleVO.class);

    // Return response
    return ResultResponse.success(articleVO);
  }
}
