package com.sunrizon.horizon.vo;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章详情VO（包含SEO元数据）
 * 扩展ArticleVO，添加SEO相关字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDetailVO extends ArticleVO {
  
  /**
   * SEO元数据
   */
  private SeoMetadata seo;
  
  /**
   * 互动统计数据
   */
  private InteractionStats stats;
  
  /**
   * SEO元数据嵌套类
   */
  @Data
  public static class SeoMetadata {
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
    private String canonicalUrl;
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String ogType;
    private String twitterCard;
    private String twitterSite;
    private String jsonLd;
    private String robotsMeta;
  }
  
  /**
   * 互动统计嵌套类
   */
  @Data
  public static class InteractionStats {
    private Long likeCount;
    private Long favoriteCount;
    private Long shareCount;
    private Long commentCount;
    
    // 当前用户的互动状态（需要登录）
    private Boolean isLiked;
    private Boolean isFavorited;
  }
  
  /**
   * Meta标签Map（用于前端直接渲染）
   */
  private Map<String, String> metaTags;
  
  /**
   * Open Graph标签Map
   */
  private Map<String, String> ogTags;
}
