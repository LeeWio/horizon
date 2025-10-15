package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.enums.ArticleStatus;
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
import com.sunrizon.horizon.service.ICacheService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

  @Resource
  private ICacheService cacheService;

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
   * Update an existing article.
   *
   * @param aid     Article ID
   * @param request Update request
   * @return {@link ResultResponse} with success or error message
   */
  @Override
  @Transactional
  public ResultResponse<String> updateArticle(String aid, UpdateArticleRequest request) {
    // 1. 参数验证
    if (StrUtil.isBlank(aid)) {
      return ResultResponse.error(ResponseCode.ARTICLE_ID_CANNOT_BE_EMPTY);
    }

    // 2. 查找文章
    Article article = articleRepository.findById(aid)
        .orElseThrow(() -> new RuntimeException("Article not found with id: " + aid));

    // 3. 更新标题（检查唯一性）
    if (StrUtil.isNotBlank(request.getTitle()) && !request.getTitle().equals(article.getTitle())) {
      article.setTitle(request.getTitle());
    }

    // 4. 更新 slug（检查唯一性）
    if (StrUtil.isNotBlank(request.getSlug()) && !request.getSlug().equals(article.getSlug())) {
      if (articleRepository.existsBySlug(request.getSlug())) {
        return ResultResponse.error(ResponseCode.ARTICLE_SLUG_EXISTS);
      }
      article.setSlug(request.getSlug());
    }

    // 5. 更新其他字段
    if (StrUtil.isNotBlank(request.getSummary())) {
      article.setSummary(request.getSummary());
    }
    if (StrUtil.isNotBlank(request.getContent())) {
      article.setContent(request.getContent());
    }
    if (StrUtil.isNotBlank(request.getCoverImage())) {
      article.setCoverImage(request.getCoverImage());
    }
    if (request.getStatus() != null) {
      article.setStatus(request.getStatus());
    }
    if (request.getIsFeatured() != null) {
      article.setIsFeatured(request.getIsFeatured());
    }

    // 6. 更新关联关系 - 分类
    if (request.getCategoryIds() != null) {
      Set<Category> categories = categoryRepository.findAllById(request.getCategoryIds())
          .stream().collect(Collectors.toSet());
      article.setCategories(categories);
    }

    // 7. 更新关联关系 - 系列
    if (request.getSeriesIds() != null) {
      Set<Series> seriesSet = seriesRepository.findAllById(request.getSeriesIds())
          .stream().collect(Collectors.toSet());
      article.setSeries(seriesSet);
    }

    // 8. 更新关联关系 - 标签
    if (request.getTagIds() != null) {
      Set<Tag> tags = tagRepository.findAllById(request.getTagIds())
          .stream().collect(Collectors.toSet());
      article.setTags(tags);
    }

    // 9. 保存更改
    articleRepository.saveAndFlush(article);

    // 10. 清除缓存
    cacheService.evict("article:" + aid);
    cacheService.evictByPattern("trending:*"); // 清除所有排行榜缓存
    log.info("已清除文章缓存: {}", aid);

    // 11. 返回响应
    return ResultResponse.success(ResponseCode.ARTICLE_UPDATED_SUCCESSFULLY);
  }

  /**
   * Delete an article by ID.
   * <p>
   * 删除后清除相关缓存
   *
   * @param aid Article ID
   * @return {@link ResultResponse} with success or error message
   */
  @Override
  @Transactional
  public ResultResponse<String> deleteArticle(String aid) {
    // 1. 参数验证
    if (StrUtil.isBlank(aid)) {
      return ResultResponse.error(ResponseCode.ARTICLE_ID_CANNOT_BE_EMPTY);
    }

    // 2. 查找文章
    Article article = articleRepository.findById(aid)
        .orElseThrow(() -> new RuntimeException("Article not found with id: " + aid));

    // 3. 删除文章（硬删除）
    articleRepository.delete(article);

    // 4. 清除缓存
    cacheService.evict("article:" + aid);
    cacheService.evictByPattern("trending:*");
    log.info("已清除已删除文章的缓存: {}", aid);

    // 5. 返回响应
    return ResultResponse.success(ResponseCode.ARTICLE_DELETED_SUCCESSFULLY);
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
   * <p>
   * 使用Redis缓存，TTL为10分钟
   *
   * @param id The article ID to find
   * @return {@link ResultResponse} with {@link ArticleVO} matching the ID
   */
  @Override
  public ResultResponse<ArticleVO> getArticleById(String id) {
    // 缓存键
    String cacheKey = "article:" + id;

    // 从缓存获取，缓存未命中时查询数据库
    ArticleVO articleVO = cacheService.getWithFallback(
        cacheKey,
        ArticleVO.class,
        () -> {
          // 数据库回调
          Article article = articleRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
          return BeanUtil.copyProperties(article, ArticleVO.class);
        },
        10,
        TimeUnit.MINUTES
    );

    return ResultResponse.success(articleVO);
  }

  /**
   * Get trending articles by view count.
   * <p>
   * 使用Redis缓存，TTL为5分钟（热门文章排行更新频率较高）
   *
   * @param timeRange Time range filter (DAY, WEEK, MONTH, ALL)
   * @param pageable  Pagination info
   * @return {@link ResultResponse} with page of trending {@link ArticleVO}
   */
  @Override
  public ResultResponse<Page<ArticleVO>> getTrendingByViews(String timeRange, Pageable pageable) {
    // 缓存键：包含时间范围和分页信息
    String cacheKey = String.format("trending:views:%s:page%d:size%d",
        timeRange, pageable.getPageNumber(), pageable.getPageSize());

    // 从缓存获取
    Page<ArticleVO> voPage = cacheService.getWithFallback(
        cacheKey,
        Page.class,
        () -> {
          // 数据库回调
          LocalDateTime startDate = calculateStartDate(timeRange);
          Page<Article> articlePage;

          if (startDate == null) {
            // All time
            articlePage = articleRepository.findByStatusOrderByViewCountDesc(ArticleStatus.PUBLISHED, pageable);
          } else {
            // With time filter
            articlePage = articleRepository.findTrendingByViews(ArticleStatus.PUBLISHED, startDate, pageable);
          }

          return articlePage.map(article -> BeanUtil.copyProperties(article, ArticleVO.class));
        },
        5,
        TimeUnit.MINUTES
    );

    return ResultResponse.success(voPage);
  }

  /**
   * Get trending articles by like count.
   *
   * @param timeRange Time range filter (DAY, WEEK, MONTH, ALL)
   * @param pageable  Pagination info
   * @return {@link ResultResponse} with page of trending {@link ArticleVO}
   */
  @Override
  public ResultResponse<Page<ArticleVO>> getTrendingByLikes(String timeRange, Pageable pageable) {
    LocalDateTime startDate = calculateStartDate(timeRange);
    Page<Article> articlePage;

    if (startDate == null) {
      // All time
      articlePage = articleRepository.findByStatusOrderByLikeCountDesc(ArticleStatus.PUBLISHED, pageable);
    } else {
      // With time filter
      articlePage = articleRepository.findTrendingByLikes(ArticleStatus.PUBLISHED, startDate, pageable);
    }

    Page<ArticleVO> voPage = articlePage.map(article -> BeanUtil.copyProperties(article, ArticleVO.class));
    return ResultResponse.success(voPage);
  }

  /**
   * Get trending articles by favorite count.
   *
   * @param timeRange Time range filter (DAY, WEEK, MONTH, ALL)
   * @param pageable  Pagination info
   * @return {@link ResultResponse} with page of trending {@link ArticleVO}
   */
  @Override
  public ResultResponse<Page<ArticleVO>> getTrendingByFavorites(String timeRange, Pageable pageable) {
    LocalDateTime startDate = calculateStartDate(timeRange);
    Page<Article> articlePage;

    if (startDate == null) {
      // All time
      articlePage = articleRepository.findByStatusOrderByFavoriteCountDesc(ArticleStatus.PUBLISHED, pageable);
    } else {
      // With time filter
      articlePage = articleRepository.findTrendingByFavorites(ArticleStatus.PUBLISHED, startDate, pageable);
    }

    Page<ArticleVO> voPage = articlePage.map(article -> BeanUtil.copyProperties(article, ArticleVO.class));
    return ResultResponse.success(voPage);
  }

  /**
   * Calculate start date based on time range.
   *
   * @param timeRange Time range (DAY, WEEK, MONTH, ALL)
   * @return Start date or null for ALL
   */
  private LocalDateTime calculateStartDate(String timeRange) {
    if (timeRange == null || "ALL".equalsIgnoreCase(timeRange)) {
      return null;
    }

    LocalDateTime now = LocalDateTime.now();
    return switch (timeRange.toUpperCase()) {
      case "DAY" -> now.minusDays(1);
      case "WEEK" -> now.minusWeeks(1);
      case "MONTH" -> now.minusMonths(1);
      default -> null;
    };
  }
}
