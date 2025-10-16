package com.sunrizon.horizon.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章SEO元数据实体类
 * 存储每篇文章的SEO优化信息
 */
@Table(name = "article_seo", indexes = {
    @Index(name = "idx_article_seo_aid", columnList = "article_id", unique = true),
    @Index(name = "idx_article_seo_canonical", columnList = "canonical_url")
})
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ArticleSeo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false, length = 36, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  /**
   * 关联的文章ID（一对一关系）
   */
  @Column(name = "article_id", nullable = false, length = 36, unique = true)
  private String articleId;

  /**
   * SEO标题（覆盖文章标题，用于<title>标签）
   * 如果为空，则使用文章原标题
   */
  @Column(name = "seo_title", length = 150)
  private String seoTitle;

  /**
   * SEO描述（用于meta description）
   * 如果为空，则使用文章摘要
   */
  @Column(name = "seo_description", length = 300)
  private String seoDescription;

  /**
   * SEO关键词（用于meta keywords）
   * 逗号分隔，例如：Java,Spring Boot,博客系统
   */
  @Column(name = "seo_keywords", length = 500)
  private String seoKeywords;

  /**
   * 规范URL（用于防止重复内容）
   * 例如：https://example.com/articles/java-tutorial
   */
  @Column(name = "canonical_url", length = 500)
  private String canonicalUrl;

  /**
   * Open Graph 标题（用于社交分享）
   */
  @Column(name = "og_title", length = 150)
  private String ogTitle;

  /**
   * Open Graph 描述
   */
  @Column(name = "og_description", length = 300)
  private String ogDescription;

  /**
   * Open Graph 图片URL
   */
  @Column(name = "og_image", length = 500)
  private String ogImage;

  /**
   * Open Graph 类型（默认：article）
   */
  @Column(name = "og_type", length = 50)
  private String ogType = "article";

  /**
   * Twitter Card 类型
   * summary, summary_large_image, app, player
   */
  @Column(name = "twitter_card", length = 50)
  private String twitterCard = "summary_large_image";

  /**
   * Twitter 站点账号
   */
  @Column(name = "twitter_site", length = 100)
  private String twitterSite;

  /**
   * 结构化数据（JSON-LD）
   * 存储 Schema.org 结构化数据
   */
  @Column(name = "schema_json", columnDefinition = "TEXT")
  private String schemaJson;

  /**
   * robots meta标签
   * 例如：index,follow 或 noindex,nofollow
   */
  @Column(name = "robots_meta", length = 100)
  private String robotsMeta = "index,follow";

  /**
   * 是否在sitemap中包含此文章
   */
  @Column(name = "include_in_sitemap", nullable = false)
  private Boolean includeInSitemap = true;

  /**
   * Sitemap优先级（0.0-1.0）
   */
  @Column(name = "sitemap_priority")
  private Double sitemapPriority = 0.5;

  /**
   * Sitemap更新频率
   * always, hourly, daily, weekly, monthly, yearly, never
   */
  @Column(name = "sitemap_changefreq", length = 20)
  private String sitemapChangefreq = "weekly";

  @Column(name = "created_at", updatable = false, nullable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
