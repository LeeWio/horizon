package com.sunrizon.horizon.constants;

/**
 * 缓存相关常量
 */
public class CacheConstants {
    
    // 缓存名称
    public static final String CACHE_USER = "user";
    public static final String CACHE_ARTICLE = "article";
    public static final String CACHE_CATEGORY = "category";
    public static final String CACHE_TAG = "tag";
    public static final String CACHE_ROLE = "role";
    public static final String CACHE_PERMISSION = "permission";
    
    // 缓存TTL（生存时间）
    public static final int CACHE_TTL_DEFAULT = 600; // 10分钟
    public static final int CACHE_TTL_USER = 1800; // 30分钟
    public static final int CACHE_TTL_ARTICLE = 3600; // 1小时
    public static final int CACHE_TTL_CATEGORY = 7200; // 2小时
    public static final int CACHE_TTL_TAG = 7200; // 2小时
    public static final int CACHE_TTL_ROLE = 3600; // 1小时
    
    // 私有构造函数，防止实例化
    private CacheConstants() {
        throw new UnsupportedOperationException("CacheConstants class cannot be instantiated");
    }
}
