package com.sunrizon.horizon.constants;

/**
 * 系统相关常量
 */
public class SystemConstants {
    
    // 分页相关
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;
    
    // 时间相关
    public static final int TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7天（秒）
    public static final int REFRESH_TOKEN_EXPIRE_TIME = 30 * 24 * 60 * 60; // 30天（秒）
    public static final int CAPTCHA_EXPIRE_TIME = 5 * 60; // 5分钟（秒）
    
    // 默认值
    public static final String DEFAULT_SITE_NAME = "Horizon Blog";
    public static final String DEFAULT_SITE_DESCRIPTION = "一个现代化的博客平台";
    
    // HTTP 头部
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    // 线程池相关
    public static final int CORE_POOL_SIZE = 5;
    public static final int MAX_POOL_SIZE = 20;
    public static final int QUEUE_CAPACITY = 100;
    public static final int KEEP_ALIVE_SECONDS = 60;
    
    // 日志操作类型
    public static final String LOG_OPERATION_CREATE = "CREATE";
    public static final String LOG_OPERATION_UPDATE = "UPDATE";
    public static final String LOG_OPERATION_DELETE = "DELETE";
    public static final String LOG_OPERATION_READ = "READ";
    public static final String LOG_OPERATION_LOGIN = "LOGIN";
    public static final String LOG_OPERATION_LOGOUT = "LOGOUT";
    
    // 私有构造函数，防止实例化
    private SystemConstants() {
        throw new UnsupportedOperationException("SystemConstants class cannot be instantiated");
    }
}
