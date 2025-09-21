package com.sunrizon.horizon.constants;

/**
 * 系统常量定义
 * 统一管理系统中的常量值
 */
public class Constants {
    
    // ==================== 用户相关常量 ====================
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int AVATAR_MAX_LENGTH = 255;
    
    // ==================== 文章相关常量 ====================
    public static final int TITLE_MIN_LENGTH = 1;
    public static final int TITLE_MAX_LENGTH = 200;
    public static final int SLUG_MAX_LENGTH = 200;
    public static final int SUMMARY_MAX_LENGTH = 1000;
    public static final int CONTENT_MAX_LENGTH = 100000;
    public static final int COVER_IMAGE_MAX_LENGTH = 500;
    
    // ==================== 分类相关常量 ====================
    public static final int CATEGORY_NAME_MIN_LENGTH = 1;
    public static final int CATEGORY_NAME_MAX_LENGTH = 100;
    public static final int CATEGORY_SLUG_MAX_LENGTH = 100;
    public static final int CATEGORY_DESCRIPTION_MAX_LENGTH = 500;
    
    // ==================== 标签相关常量 ====================
    public static final int TAG_NAME_MIN_LENGTH = 1;
    public static final int TAG_NAME_MAX_LENGTH = 100;
    public static final int TAG_SLUG_MAX_LENGTH = 100;
    public static final int TAG_DESCRIPTION_MAX_LENGTH = 500;
    
    // ==================== 角色相关常量 ====================
    public static final int ROLE_NAME_MIN_LENGTH = 1;
    public static final int ROLE_NAME_MAX_LENGTH = 50;
    public static final int ROLE_DESCRIPTION_MAX_LENGTH = 255;
    
    // ==================== 评论相关常量 ====================
    public static final int COMMENT_CONTENT_MIN_LENGTH = 1;
    public static final int COMMENT_CONTENT_MAX_LENGTH = 2000;
    public static final int COMMENT_AUTHOR_NAME_MAX_LENGTH = 100;
    public static final int COMMENT_AUTHOR_EMAIL_MAX_LENGTH = 100;
    public static final int COMMENT_AUTHOR_WEBSITE_MAX_LENGTH = 255;
    
    // ==================== 文件相关常量 ====================
    public static final long FILE_MAX_SIZE = 10 * 1024 * 1024; // 10MB
    public static final int FILE_NAME_MAX_LENGTH = 255;
    public static final int FILE_PATH_MAX_LENGTH = 500;
    public static final int MIME_TYPE_MAX_LENGTH = 100;
    
    // 支持的文件类型
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    };
    
    public static final String[] ALLOWED_DOCUMENT_TYPES = {
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "text/csv"
    };
    
    // ==================== 缓存相关常量 ====================
    public static final String CACHE_USER = "user";
    public static final String CACHE_ARTICLE = "article";
    public static final String CACHE_CATEGORY = "category";
    public static final String CACHE_TAG = "tag";
    public static final String CACHE_ROLE = "role";
    public static final String CACHE_PERMISSION = "permission";
    
    public static final int CACHE_TTL_DEFAULT = 600; // 10分钟
    public static final int CACHE_TTL_USER = 1800; // 30分钟
    public static final int CACHE_TTL_ARTICLE = 3600; // 1小时
    public static final int CACHE_TTL_CATEGORY = 7200; // 2小时
    public static final int CACHE_TTL_TAG = 7200; // 2小时
    public static final int CACHE_TTL_ROLE = 3600; // 1小时
    
    // ==================== 分页相关常量 ====================
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;
    
    // ==================== 时间相关常量 ====================
    public static final int TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7天（秒）
    public static final int REFRESH_TOKEN_EXPIRE_TIME = 30 * 24 * 60 * 60; // 30天（秒）
    public static final int CAPTCHA_EXPIRE_TIME = 5 * 60; // 5分钟（秒）
    
    // ==================== 正则表达式常量 ====================
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,50}$";
    public static final String SLUG_REGEX = "^[a-z0-9-]+$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    // ==================== 默认值常量 ====================
    public static final String DEFAULT_AVATAR = "/static/images/default-avatar.png";
    public static final String DEFAULT_COVER_IMAGE = "/static/images/default-cover.jpg";
    public static final String DEFAULT_SITE_NAME = "Horizon Blog";
    public static final String DEFAULT_SITE_DESCRIPTION = "一个现代化的博客平台";
    
    // ==================== 状态常量 ====================
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_BANNED = "BANNED";
    public static final String STATUS_DELETED = "DELETED";
    
    // ==================== 权限常量 ====================
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_EDITOR = "EDITOR";
    public static final String ROLE_USER = "USER";
    
    // ==================== 文件路径常量 ====================
    public static final String UPLOAD_PATH = "uploads";
    public static final String AVATAR_PATH = "uploads/avatars";
    public static final String ARTICLE_IMAGE_PATH = "uploads/articles";
    public static final String TEMP_PATH = "uploads/temp";
    
    // ==================== 日志相关常量 ====================
    public static final String LOG_OPERATION_CREATE = "CREATE";
    public static final String LOG_OPERATION_UPDATE = "UPDATE";
    public static final String LOG_OPERATION_DELETE = "DELETE";
    public static final String LOG_OPERATION_READ = "READ";
    public static final String LOG_OPERATION_LOGIN = "LOGIN";
    public static final String LOG_OPERATION_LOGOUT = "LOGOUT";
    
    // ==================== 线程池相关常量 ====================
    public static final int CORE_POOL_SIZE = 5;
    public static final int MAX_POOL_SIZE = 20;
    public static final int QUEUE_CAPACITY = 100;
    public static final int KEEP_ALIVE_SECONDS = 60;
    
    // ==================== 其他常量 ====================
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    // 私有构造函数，防止实例化
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
