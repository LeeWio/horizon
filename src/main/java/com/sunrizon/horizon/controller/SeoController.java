package com.sunrizon.horizon.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sunrizon.horizon.pojo.ArticleSeo;
import com.sunrizon.horizon.pojo.SeoConfig;
import com.sunrizon.horizon.service.ISeoService;
import com.sunrizon.horizon.utils.ResultResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * SEO控制器
 * 提供SEO相关API接口
 */
@RestController
@RequestMapping("/api/seo")
@Slf4j
@Tag(name = "SEO", description = "SEO optimization APIs")
public class SeoController {

  @Resource
  private ISeoService seoService;

  /**
   * 获取或创建文章SEO元数据
   */
  @GetMapping("/article/{articleId}")
  @Operation(summary = "Get or create article SEO metadata")
  public ResultResponse<ArticleSeo> getArticleSeo(
      @Parameter(description = "Article ID") @PathVariable String articleId) {
    return seoService.getOrCreateArticleSeo(articleId);
  }

  /**
   * 更新文章SEO元数据
   */
  @PutMapping("/article/{articleId}")
  @Operation(summary = "Update article SEO metadata")
  public ResultResponse<ArticleSeo> updateArticleSeo(
      @Parameter(description = "Article ID") @PathVariable String articleId,
      @RequestBody ArticleSeo articleSeo) {
    return seoService.updateArticleSeo(articleId, articleSeo);
  }

  /**
   * 删除文章SEO元数据
   */
  @DeleteMapping("/article/{articleId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Delete article SEO metadata")
  public ResultResponse<String> deleteArticleSeo(
      @Parameter(description = "Article ID") @PathVariable String articleId) {
    return seoService.deleteArticleSeo(articleId);
  }

  /**
   * 生成sitemap.xml
   */
  @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(summary = "Generate sitemap.xml")
  public String getSitemap(
      @Parameter(description = "Base URL (e.g., https://example.com)") 
      @RequestParam(required = false, defaultValue = "https://example.com") String baseUrl) {
    ResultResponse<String> result = seoService.generateSitemap(baseUrl);
    return result.isSuccess() ? result.getData() : "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset></urlset>";
  }

  /**
   * 生成sitemap.xml（JSON格式返回，用于预览）
   */
  @GetMapping("/sitemap")
  @Operation(summary = "Generate sitemap (JSON response)")
  public ResultResponse<String> getSitemapJson(
      @Parameter(description = "Base URL (e.g., https://example.com)") 
      @RequestParam(required = false, defaultValue = "https://example.com") String baseUrl) {
    return seoService.generateSitemap(baseUrl);
  }

  /**
   * 获取robots.txt
   */
  @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Get robots.txt")
  public String getRobotsTxt() {
    ResultResponse<String> result = seoService.getRobotsTxt();
    return result.isSuccess() ? result.getData() : "User-agent: *\nAllow: /";
  }

  /**
   * 获取robots.txt（JSON格式返回，用于预览）
   */
  @GetMapping("/robots")
  @Operation(summary = "Get robots.txt (JSON response)")
  public ResultResponse<String> getRobotsJson() {
    return seoService.getRobotsTxt();
  }

  /**
   * 更新robots.txt
   */
  @PutMapping("/robots")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update robots.txt")
  public ResultResponse<String> updateRobotsTxt(@RequestBody Map<String, String> request) {
    String content = request.get("content");
    return seoService.updateRobotsTxt(content);
  }

  /**
   * 获取SEO配置
   */
  @GetMapping("/config/{configKey}")
  @Operation(summary = "Get SEO configuration")
  public ResultResponse<SeoConfig> getSeoConfig(
      @Parameter(description = "Config key") @PathVariable String configKey) {
    return seoService.getSeoConfig(configKey);
  }

  /**
   * 保存SEO配置
   */
  @PostMapping("/config")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Save SEO configuration")
  public ResultResponse<SeoConfig> saveSeoConfig(@RequestBody SeoConfig seoConfig) {
    return seoService.saveSeoConfig(seoConfig);
  }

  /**
   * 获取所有SEO配置
   */
  @GetMapping("/configs")
  @Operation(summary = "Get all SEO configurations")
  public ResultResponse<Map<String, Object>> getAllSeoConfigs() {
    return seoService.getAllSeoConfigs();
  }

  /**
   * 生成文章JSON-LD结构化数据
   */
  @GetMapping("/article/{articleId}/json-ld")
  @Operation(summary = "Generate article JSON-LD structured data")
  public ResultResponse<String> getArticleJsonLd(
      @Parameter(description = "Article ID") @PathVariable String articleId) {
    return seoService.generateArticleJsonLd(articleId);
  }

  /**
   * 批量生成文章SEO元数据
   */
  @PostMapping("/batch-generate")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Batch generate SEO metadata for published articles")
  public ResultResponse<Integer> batchGenerateArticleSeo() {
    return seoService.batchGenerateArticleSeo();
  }
}
