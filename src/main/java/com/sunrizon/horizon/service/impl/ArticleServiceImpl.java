package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.pojo.ArticleSeo;
import com.sunrizon.horizon.pojo.Category;
import com.sunrizon.horizon.pojo.Series;
import com.sunrizon.horizon.pojo.Tag;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.repository.ArticleSeoRepository;
import com.sunrizon.horizon.repository.CategoryRepository;
import com.sunrizon.horizon.repository.SeriesRepository;
import com.sunrizon.horizon.repository.TagRepository;
import com.sunrizon.horizon.constants.RedisContants;
import com.sunrizon.horizon.service.IArticleService;
import com.sunrizon.horizon.service.ICacheService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.XssUtil;
import com.sunrizon.horizon.vo.ArticleDetailVO;
import com.sunrizon.horizon.vo.ArticleVO;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  private ArticleSeoRepository articleSeoRepository;

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
    
    // Handle scheduled publishing
    if (article.getStatus() == ArticleStatus.SCHEDULED && article.getPublishTime() == null) {
      return ResultResponse.error(ResponseCode.ARTICLE_PUBLISH_TIME_REQUIRED_FOR_SCHEDULED);
    }
    
    // If publish time is set but status is not SCHEDULED, adjust status
    if (article.getPublishTime() != null && article.getStatus() != ArticleStatus.SCHEDULED) {
      article.setStatus(ArticleStatus.SCHEDULED);
    }
    
    // If status is SCHEDULED but publish time is in the past, publish immediately
    if (article.getStatus() == ArticleStatus.SCHEDULED && article.getPublishTime() != null 
        && article.getPublishTime().isBefore(LocalDateTime.now())) {
      article.setStatus(ArticleStatus.PUBLISHED);
    }

    // XSS防护：清理文章内容中的恶意代码
    if (StrUtil.isNotBlank(article.getContent())) {
      article.setContent(XssUtil.cleanArticle(article.getContent()));
    }
    if (StrUtil.isNotBlank(article.getSummary())) {
      article.setSummary(XssUtil.cleanRelaxed(article.getSummary()));
    }
    if (StrUtil.isNotBlank(article.getTitle())) {
      article.setTitle(XssUtil.cleanUserInput(article.getTitle()));
    }

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

    // 5. 更新其他字段（带XSS防护）
    if (StrUtil.isNotBlank(request.getSummary())) {
      article.setSummary(XssUtil.cleanRelaxed(request.getSummary()));
    }
    if (StrUtil.isNotBlank(request.getContent())) {
      article.setContent(XssUtil.cleanArticle(request.getContent()));
    }
    if (StrUtil.isNotBlank(request.getCoverImage())) {
      article.setCoverImage(request.getCoverImage());
    }
    
    // Handle scheduled publishing
    if (request.getStatus() == ArticleStatus.SCHEDULED && request.getPublishTime() == null 
        && article.getPublishTime() == null) {
      return ResultResponse.error(ResponseCode.ARTICLE_PUBLISH_TIME_REQUIRED_FOR_SCHEDULED);
    }
    
    // If publish time is set but status is not SCHEDULED, adjust status
    if (request.getPublishTime() != null && request.getStatus() != ArticleStatus.SCHEDULED) {
      article.setStatus(ArticleStatus.SCHEDULED);
    }
    
    // If status is SCHEDULED but publish time is in the past, publish immediately
    if (request.getStatus() == ArticleStatus.SCHEDULED && request.getPublishTime() != null 
        && request.getPublishTime().isBefore(LocalDateTime.now())) {
      article.setStatus(ArticleStatus.PUBLISHED);
    } else if (request.getStatus() != null) {
      article.setStatus(request.getStatus());
    }
    
    // Update publish time if provided
    if (request.getPublishTime() != null) {
      article.setPublishTime(request.getPublishTime());
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
    cacheService.evict(String.format(RedisContants.ARTICLE_CACHE_KEY_FORMAT, aid));
    cacheService.evictByPattern(RedisContants.TRENDING_PATTERN); // 清除所有排行榜缓存
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
    cacheService.evict(String.format(RedisContants.ARTICLE_CACHE_KEY_FORMAT, aid));
    cacheService.evictByPattern(RedisContants.TRENDING_PATTERN);
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
    // 缓存键：使用常量格式化
    String cacheKey = String.format(RedisContants.ARTICLE_CACHE_KEY_FORMAT, id);

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
        RedisContants.CACHE_ARTICLE_TTL_SECONDS,
        TimeUnit.SECONDS
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
    // 缓存键：使用常量格式化
    String cacheKey = String.format(RedisContants.TRENDING_VIEWS_KEY_FORMAT,
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
        RedisContants.CACHE_TRENDING_TTL_SECONDS,
        TimeUnit.SECONDS
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

  /**
   * Get article detail with SEO metadata.
   *
   * @param id Article ID
   * @return {@link ResultResponse} with {@link ArticleDetailVO} including SEO metadata
   */
  @Override
  public ResultResponse<ArticleDetailVO> getArticleDetailWithSeo(String id) {
    if (StrUtil.isBlank(id)) {
      return ResultResponse.error(ResponseCode.ARTICLE_ID_CANNOT_BE_EMPTY);
    }

    // Get article
    Optional<Article> articleOpt = articleRepository.findById(id);
    if (articleOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND);
    }

    Article article = articleOpt.get();

    // Convert to ArticleDetailVO
    ArticleDetailVO detailVO = BeanUtil.copyProperties(article, ArticleDetailVO.class);

    // Get SEO metadata if exists
    Optional<ArticleSeo> seoOpt = articleSeoRepository.findByArticleId(id);
    if (seoOpt.isPresent()) {
      ArticleSeo seo = seoOpt.get();
      ArticleDetailVO.SeoMetadata seoMetadata = new ArticleDetailVO.SeoMetadata();
      seoMetadata.setSeoTitle(seo.getSeoTitle());
      seoMetadata.setSeoDescription(seo.getSeoDescription());
      seoMetadata.setSeoKeywords(seo.getSeoKeywords());
      seoMetadata.setCanonicalUrl(seo.getCanonicalUrl());
      seoMetadata.setOgTitle(seo.getOgTitle());
      seoMetadata.setOgDescription(seo.getOgDescription());
      seoMetadata.setOgImage(seo.getOgImage());
      seoMetadata.setOgType(seo.getOgType());
      seoMetadata.setTwitterCard(seo.getTwitterCard());
      seoMetadata.setTwitterSite(seo.getTwitterSite());
      seoMetadata.setJsonLd(seo.getSchemaJson());
      seoMetadata.setRobotsMeta(seo.getRobotsMeta());
      detailVO.setSeo(seoMetadata);

      // Generate meta tags map
      Map<String, String> metaTags = new HashMap<>();
      metaTags.put("title", StrUtil.isNotBlank(seo.getSeoTitle()) ? seo.getSeoTitle() : article.getTitle());
      metaTags.put("description", StrUtil.isNotBlank(seo.getSeoDescription()) ? seo.getSeoDescription() : article.getSummary());
      if (StrUtil.isNotBlank(seo.getSeoKeywords())) {
        metaTags.put("keywords", seo.getSeoKeywords());
      }
      if (StrUtil.isNotBlank(seo.getCanonicalUrl())) {
        metaTags.put("canonical", seo.getCanonicalUrl());
      }
      metaTags.put("robots", seo.getRobotsMeta());
      detailVO.setMetaTags(metaTags);

      // Generate Open Graph tags map
      Map<String, String> ogTags = new HashMap<>();
      ogTags.put("og:title", StrUtil.isNotBlank(seo.getOgTitle()) ? seo.getOgTitle() : article.getTitle());
      ogTags.put("og:description", StrUtil.isNotBlank(seo.getOgDescription()) ? seo.getOgDescription() : article.getSummary());
      ogTags.put("og:type", seo.getOgType());
      if (StrUtil.isNotBlank(seo.getOgImage())) {
        ogTags.put("og:image", seo.getOgImage());
      }
      ogTags.put("twitter:card", seo.getTwitterCard());
      if (StrUtil.isNotBlank(seo.getTwitterSite())) {
        ogTags.put("twitter:site", seo.getTwitterSite());
      }
      detailVO.setOgTags(ogTags);
    } else {
      // Generate default meta tags from article
      Map<String, String> metaTags = new HashMap<>();
      metaTags.put("title", article.getTitle());
      if (StrUtil.isNotBlank(article.getSummary())) {
        metaTags.put("description", article.getSummary());
      }
      metaTags.put("robots", "index,follow");
      detailVO.setMetaTags(metaTags);

      Map<String, String> ogTags = new HashMap<>();
      ogTags.put("og:title", article.getTitle());
      if (StrUtil.isNotBlank(article.getSummary())) {
        ogTags.put("og:description", article.getSummary());
      }
      ogTags.put("og:type", "article");
      if (StrUtil.isNotBlank(article.getCoverImage())) {
        ogTags.put("og:image", article.getCoverImage());
      }
      ogTags.put("twitter:card", "summary_large_image");
      detailVO.setOgTags(ogTags);
    }

    // Set interaction stats
    ArticleDetailVO.InteractionStats stats = new ArticleDetailVO.InteractionStats();
    stats.setLikeCount(article.getLikeCount());
    stats.setFavoriteCount(article.getFavoriteCount());
    stats.setShareCount(article.getShareCount());
    // Comment count would need to be fetched from CommentRepository
    stats.setCommentCount(0L); // TODO: Implement comment count query
    detailVO.setStats(stats);

    return ResultResponse.success(detailVO);
  }
}
