package com.sunrizon.horizon.service.impl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.pojo.ArticleSeo;
import com.sunrizon.horizon.pojo.SeoConfig;
import com.sunrizon.horizon.pojo.Tag;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.repository.ArticleSeoRepository;
import com.sunrizon.horizon.repository.SeoConfigRepository;
import com.sunrizon.horizon.service.ISeoService;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.utils.ResultResponse;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * SEO服务实现类
 */
@Service
@Slf4j
public class SeoServiceImpl implements ISeoService {

  @Resource
  private ArticleSeoRepository articleSeoRepository;

  @Resource
  private SeoConfigRepository seoConfigRepository;

  @Resource
  private ArticleRepository articleRepository;

  private static final String DEFAULT_ROBOTS_TXT = """
      User-agent: *
      Allow: /
      Disallow: /api/
      Disallow: /admin/
      
      Sitemap: {baseUrl}/sitemap.xml
      """;

  private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

  @Override
  @Transactional
  public ResultResponse<ArticleSeo> getOrCreateArticleSeo(String articleId) {
    if (StrUtil.isBlank(articleId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Article ID cannot be empty");
    }

    // Check if article exists
    Optional<Article> articleOpt = articleRepository.findById(articleId);
    if (articleOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.NOT_FOUND, "Article not found");
    }

    Article article = articleOpt.get();

    // Try to find existing SEO data
    Optional<ArticleSeo> existingSeo = articleSeoRepository.findByArticleId(articleId);
    if (existingSeo.isPresent()) {
      return ResultResponse.success(existingSeo.get());
    }

    // Create new SEO data with defaults from article
    ArticleSeo articleSeo = new ArticleSeo();
    articleSeo.setArticleId(articleId);
    articleSeo.setSeoTitle(article.getTitle());
    articleSeo.setSeoDescription(truncate(article.getSummary(), 300));
    
    // Generate keywords from tags
    if (article.getTags() != null && !article.getTags().isEmpty()) {
      String keywords = article.getTags().stream()
          .map(Tag::getName)
          .collect(Collectors.joining(","));
      articleSeo.setSeoKeywords(keywords);
    }

    // Set Open Graph defaults
    articleSeo.setOgTitle(article.getTitle());
    articleSeo.setOgDescription(truncate(article.getSummary(), 300));
    articleSeo.setOgImage(article.getCoverImage());
    articleSeo.setOgType("article");

    // Set default values
    articleSeo.setTwitterCard("summary_large_image");
    articleSeo.setRobotsMeta("index,follow");
    articleSeo.setIncludeInSitemap(article.getStatus() == ArticleStatus.PUBLISHED);
    articleSeo.setSitemapPriority(0.7);
    articleSeo.setSitemapChangefreq("weekly");

    ArticleSeo saved = articleSeoRepository.save(articleSeo);
    log.info("Created SEO metadata for article: {}", articleId);

    return ResultResponse.success(saved);
  }

  @Override
  @Transactional
  public ResultResponse<ArticleSeo> updateArticleSeo(String articleId, ArticleSeo articleSeo) {
    if (StrUtil.isBlank(articleId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Article ID cannot be empty");
    }

    // Check if article exists
    if (!articleRepository.existsById(articleId)) {
      return ResultResponse.error(ResponseCode.NOT_FOUND, "Article not found");
    }

    // Find or create SEO data
    ArticleSeo existingSeo = articleSeoRepository.findByArticleId(articleId)
        .orElse(new ArticleSeo());

    existingSeo.setArticleId(articleId);
    
    // Update fields if provided
    if (StrUtil.isNotBlank(articleSeo.getSeoTitle())) {
      existingSeo.setSeoTitle(articleSeo.getSeoTitle());
    }
    if (StrUtil.isNotBlank(articleSeo.getSeoDescription())) {
      existingSeo.setSeoDescription(articleSeo.getSeoDescription());
    }
    if (StrUtil.isNotBlank(articleSeo.getSeoKeywords())) {
      existingSeo.setSeoKeywords(articleSeo.getSeoKeywords());
    }
    if (StrUtil.isNotBlank(articleSeo.getCanonicalUrl())) {
      existingSeo.setCanonicalUrl(articleSeo.getCanonicalUrl());
    }
    if (StrUtil.isNotBlank(articleSeo.getOgTitle())) {
      existingSeo.setOgTitle(articleSeo.getOgTitle());
    }
    if (StrUtil.isNotBlank(articleSeo.getOgDescription())) {
      existingSeo.setOgDescription(articleSeo.getOgDescription());
    }
    if (StrUtil.isNotBlank(articleSeo.getOgImage())) {
      existingSeo.setOgImage(articleSeo.getOgImage());
    }
    if (StrUtil.isNotBlank(articleSeo.getOgType())) {
      existingSeo.setOgType(articleSeo.getOgType());
    }
    if (StrUtil.isNotBlank(articleSeo.getTwitterCard())) {
      existingSeo.setTwitterCard(articleSeo.getTwitterCard());
    }
    if (StrUtil.isNotBlank(articleSeo.getTwitterSite())) {
      existingSeo.setTwitterSite(articleSeo.getTwitterSite());
    }
    if (StrUtil.isNotBlank(articleSeo.getSchemaJson())) {
      existingSeo.setSchemaJson(articleSeo.getSchemaJson());
    }
    if (StrUtil.isNotBlank(articleSeo.getRobotsMeta())) {
      existingSeo.setRobotsMeta(articleSeo.getRobotsMeta());
    }
    if (articleSeo.getIncludeInSitemap() != null) {
      existingSeo.setIncludeInSitemap(articleSeo.getIncludeInSitemap());
    }
    if (articleSeo.getSitemapPriority() != null) {
      existingSeo.setSitemapPriority(articleSeo.getSitemapPriority());
    }
    if (StrUtil.isNotBlank(articleSeo.getSitemapChangefreq())) {
      existingSeo.setSitemapChangefreq(articleSeo.getSitemapChangefreq());
    }

    ArticleSeo updated = articleSeoRepository.save(existingSeo);
    log.info("Updated SEO metadata for article: {}", articleId);

    return ResultResponse.success(updated);
  }

  @Override
  @Transactional
  public ResultResponse<String> deleteArticleSeo(String articleId) {
    if (StrUtil.isBlank(articleId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Article ID cannot be empty");
    }

    articleSeoRepository.deleteByArticleId(articleId);
    log.info("Deleted SEO metadata for article: {}", articleId);

    return ResultResponse.success("SEO metadata deleted successfully");
  }

  @Override
  public ResultResponse<String> generateSitemap(String baseUrl) {
    if (StrUtil.isBlank(baseUrl)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Base URL cannot be empty");
    }

    // Get all published articles
    List<Article> publishedArticles = articleRepository.findByStatus(ArticleStatus.PUBLISHED);
    
    // Get SEO data for these articles
    List<String> articleIds = publishedArticles.stream()
        .map(Article::getAid)
        .collect(Collectors.toList());
    
    Map<String, ArticleSeo> seoMap = articleSeoRepository.findByArticleIdIn(articleIds)
        .stream()
        .collect(Collectors.toMap(ArticleSeo::getArticleId, seo -> seo));

    StringBuilder sitemap = new StringBuilder();
    sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

    // Add homepage
    sitemap.append("  <url>\n");
    sitemap.append("    <loc>").append(baseUrl).append("/</loc>\n");
    sitemap.append("    <changefreq>daily</changefreq>\n");
    sitemap.append("    <priority>1.0</priority>\n");
    sitemap.append("  </url>\n");

    // Add articles
    for (Article article : publishedArticles) {
      ArticleSeo seo = seoMap.get(article.getAid());
      
      // Skip if SEO data says not to include
      if (seo != null && !seo.getIncludeInSitemap()) {
        continue;
      }

      sitemap.append("  <url>\n");
      
      // Use canonical URL if available, otherwise construct from slug or ID
      String loc = baseUrl + "/articles/";
      if (seo != null && StrUtil.isNotBlank(seo.getCanonicalUrl())) {
        loc = seo.getCanonicalUrl();
      } else if (StrUtil.isNotBlank(article.getSlug())) {
        loc += article.getSlug();
      } else {
        loc += article.getAid();
      }
      
      sitemap.append("    <loc>").append(loc).append("</loc>\n");
      
      // Last modified date
      String lastmod = article.getUpdatedAt()
          .atZone(ZoneId.systemDefault())
          .format(ISO_FORMATTER);
      sitemap.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
      
      // Change frequency and priority from SEO data or defaults
      String changefreq = (seo != null && StrUtil.isNotBlank(seo.getSitemapChangefreq())) 
          ? seo.getSitemapChangefreq() : "weekly";
      double priority = (seo != null && seo.getSitemapPriority() != null) 
          ? seo.getSitemapPriority() : 0.7;
      
      sitemap.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
      sitemap.append("    <priority>").append(priority).append("</priority>\n");
      sitemap.append("  </url>\n");
    }

    sitemap.append("</urlset>");

    log.info("Generated sitemap with {} articles", publishedArticles.size());
    return ResultResponse.success(sitemap.toString());
  }

  @Override
  public ResultResponse<String> getRobotsTxt() {
    // Try to get custom robots.txt from config
    Optional<SeoConfig> configOpt = seoConfigRepository.findByConfigKey("robots_txt");
    
    if (configOpt.isPresent() && StrUtil.isNotBlank(configOpt.get().getConfigValue())) {
      return ResultResponse.success(configOpt.get().getConfigValue());
    }

    // Return default robots.txt
    return ResultResponse.success(DEFAULT_ROBOTS_TXT);
  }

  @Override
  @Transactional
  public ResultResponse<String> updateRobotsTxt(String content) {
    if (StrUtil.isBlank(content)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Robots.txt content cannot be empty");
    }

    SeoConfig config = seoConfigRepository.findByConfigKey("robots_txt")
        .orElse(new SeoConfig());
    
    config.setConfigKey("robots_txt");
    config.setConfigValue(content);
    config.setDescription("Custom robots.txt configuration");
    config.setConfigGroup("basic");
    config.setIsEnabled(true);

    seoConfigRepository.save(config);
    log.info("Updated robots.txt configuration");

    return ResultResponse.success("Robots.txt updated successfully");
  }

  @Override
  public ResultResponse<SeoConfig> getSeoConfig(String configKey) {
    if (StrUtil.isBlank(configKey)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Config key cannot be empty");
    }

    Optional<SeoConfig> configOpt = seoConfigRepository.findByConfigKey(configKey);
    if (configOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.NOT_FOUND, "SEO config not found");
    }

    return ResultResponse.success(configOpt.get());
  }

  @Override
  @Transactional
  public ResultResponse<SeoConfig> saveSeoConfig(SeoConfig seoConfig) {
    if (seoConfig == null || StrUtil.isBlank(seoConfig.getConfigKey())) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Invalid SEO config");
    }

    SeoConfig existing = seoConfigRepository.findByConfigKey(seoConfig.getConfigKey())
        .orElse(new SeoConfig());
    
    existing.setConfigKey(seoConfig.getConfigKey());
    existing.setConfigValue(seoConfig.getConfigValue());
    existing.setDescription(seoConfig.getDescription());
    existing.setConfigGroup(seoConfig.getConfigGroup());
    existing.setIsEnabled(seoConfig.getIsEnabled() != null ? seoConfig.getIsEnabled() : true);

    SeoConfig saved = seoConfigRepository.save(existing);
    log.info("Saved SEO config: {}", seoConfig.getConfigKey());

    return ResultResponse.success(saved);
  }

  @Override
  public ResultResponse<Map<String, Object>> getAllSeoConfigs() {
    List<SeoConfig> allConfigs = seoConfigRepository.findByIsEnabledTrue();
    
    Map<String, Object> result = new HashMap<>();
    
    // Group by config group
    Map<String, List<SeoConfig>> grouped = allConfigs.stream()
        .collect(Collectors.groupingBy(
            config -> StrUtil.isBlank(config.getConfigGroup()) ? "other" : config.getConfigGroup()
        ));
    
    result.put("configs", grouped);
    result.put("total", allConfigs.size());
    
    return ResultResponse.success(result);
  }

  @Override
  public ResultResponse<String> generateArticleJsonLd(String articleId) {
    if (StrUtil.isBlank(articleId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Article ID cannot be empty");
    }

    Optional<Article> articleOpt = articleRepository.findById(articleId);
    if (articleOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.NOT_FOUND, "Article not found");
    }

    Article article = articleOpt.get();
    Optional<ArticleSeo> seoOpt = articleSeoRepository.findByArticleId(articleId);

    Map<String, Object> jsonLd = new LinkedHashMap<>();
    jsonLd.put("@context", "https://schema.org");
    jsonLd.put("@type", "Article");
    jsonLd.put("headline", article.getTitle());
    
    if (StrUtil.isNotBlank(article.getSummary())) {
      jsonLd.put("description", article.getSummary());
    }
    
    if (StrUtil.isNotBlank(article.getCoverImage())) {
      jsonLd.put("image", article.getCoverImage());
    }
    
    jsonLd.put("datePublished", article.getCreatedAt().toString());
    jsonLd.put("dateModified", article.getUpdatedAt().toString());
    
    // Add author information (simplified)
    Map<String, Object> author = new LinkedHashMap<>();
    author.put("@type", "Person");
    author.put("name", "Author"); // Could be enhanced to fetch actual author name
    jsonLd.put("author", author);
    
    // Add publisher information from config
    Optional<SeoConfig> siteNameConfig = seoConfigRepository.findByConfigKey("site_name");
    if (siteNameConfig.isPresent()) {
      Map<String, Object> publisher = new LinkedHashMap<>();
      publisher.put("@type", "Organization");
      publisher.put("name", siteNameConfig.get().getConfigValue());
      jsonLd.put("publisher", publisher);
    }

    // Add keywords from SEO data
    if (seoOpt.isPresent() && StrUtil.isNotBlank(seoOpt.get().getSeoKeywords())) {
      String[] keywords = seoOpt.get().getSeoKeywords().split(",");
      jsonLd.put("keywords", Arrays.asList(keywords));
    }

    try {
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonLd);
      return ResultResponse.success(json);
    } catch (JsonProcessingException e) {
      log.error("Failed to generate JSON-LD", e);
      return ResultResponse.error(ResponseCode.INTERNAL_ERROR, "Failed to generate JSON-LD");
    }
  }

  @Override
  @Transactional
  public ResultResponse<Integer> batchGenerateArticleSeo() {
    List<Article> publishedArticles = articleRepository.findByStatus(ArticleStatus.PUBLISHED);
    
    int count = 0;
    for (Article article : publishedArticles) {
      // Skip if SEO data already exists
      if (articleSeoRepository.existsByArticleId(article.getAid())) {
        continue;
      }
      
      // Create SEO data
      ResultResponse<ArticleSeo> result = getOrCreateArticleSeo(article.getAid());
      if (result.isSuccess()) {
        count++;
      }
    }
    
    log.info("Batch generated SEO metadata for {} articles", count);
    return ResultResponse.success(count);
  }

  /**
   * Truncate string to max length with ellipsis
   */
  private String truncate(String text, int maxLength) {
    if (StrUtil.isBlank(text)) {
      return "";
    }
    if (text.length() <= maxLength) {
      return text;
    }
    return text.substring(0, maxLength - 3) + "...";
  }
}
