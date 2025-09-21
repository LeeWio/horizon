package com.sunrizon.horizon.constants;

/**
 * 文章相关常量
 */
public class ArticleConstants {
    
    // 文章内容相关
    public static final int TITLE_MIN_LENGTH = 1;
    public static final int TITLE_MAX_LENGTH = 200;
    public static final int SLUG_MAX_LENGTH = 200;
    public static final int SUMMARY_MAX_LENGTH = 1000;
    public static final int CONTENT_MAX_LENGTH = 100000;
    public static final String SLUG_REGEX = "^[a-z0-9-]+$";
    
    // 封面图片相关
    public static final int COVER_IMAGE_MAX_LENGTH = 500;
    public static final String DEFAULT_COVER_IMAGE = "/static/images/default-cover.jpg";
    public static final String ARTICLE_IMAGE_PATH = "uploads/articles";
    
    // 私有构造函数，防止实例化
    private ArticleConstants() {
        throw new UnsupportedOperationException("ArticleConstants class cannot be instantiated");
    }
}
