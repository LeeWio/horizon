package com.sunrizon.horizon.service;

import java.util.Map;

import com.sunrizon.horizon.pojo.ArticleSeo;
import com.sunrizon.horizon.pojo.SeoConfig;
import com.sunrizon.horizon.utils.ResultResponse;

/**
 * SEO服务接口
 * 提供SEO元数据生成、sitemap生成、robots.txt配置等功能
 */
public interface ISeoService {

  /**
   * 根据文章ID获取或创建SEO元数据
   *
   * @param articleId 文章ID
   * @return SEO元数据
   */
  ResultResponse<ArticleSeo> getOrCreateArticleSeo(String articleId);

  /**
   * 更新文章SEO元数据
   *
   * @param articleId 文章ID
   * @param articleSeo SEO元数据
   * @return 更新后的SEO元数据
   */
  ResultResponse<ArticleSeo> updateArticleSeo(String articleId, ArticleSeo articleSeo);

  /**
   * 删除文章SEO元数据
   *
   * @param articleId 文章ID
   * @return 删除结果
   */
  ResultResponse<String> deleteArticleSeo(String articleId);

  /**
   * 生成sitemap.xml内容
   *
   * @param baseUrl 网站基础URL（例如：https://example.com）
   * @return sitemap XML字符串
   */
  ResultResponse<String> generateSitemap(String baseUrl);

  /**
   * 获取robots.txt内容
   *
   * @return robots.txt内容
   */
  ResultResponse<String> getRobotsTxt();

  /**
   * 更新robots.txt内容
   *
   * @param content robots.txt内容
   * @return 更新结果
   */
  ResultResponse<String> updateRobotsTxt(String content);

  /**
   * 获取SEO配置
   *
   * @param configKey 配置键
   * @return SEO配置
   */
  ResultResponse<SeoConfig> getSeoConfig(String configKey);

  /**
   * 更新或创建SEO配置
   *
   * @param seoConfig SEO配置
   * @return 更新后的配置
   */
  ResultResponse<SeoConfig> saveSeoConfig(SeoConfig seoConfig);

  /**
   * 获取所有SEO配置（按分组）
   *
   * @return 分组的SEO配置Map
   */
  ResultResponse<Map<String, Object>> getAllSeoConfigs();

  /**
   * 为文章生成JSON-LD结构化数据
   *
   * @param articleId 文章ID
   * @return JSON-LD字符串
   */
  ResultResponse<String> generateArticleJsonLd(String articleId);

  /**
   * 批量生成文章SEO元数据（为没有SEO配置的已发布文章自动生成）
   *
   * @return 生成数量
   */
  ResultResponse<Integer> batchGenerateArticleSeo();
}
