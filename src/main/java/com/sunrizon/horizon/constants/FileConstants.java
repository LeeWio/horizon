package com.sunrizon.horizon.constants;

/**
 * 文件相关常量
 */
public class FileConstants {
    
    // 文件大小相关
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
    
    // 文件路径相关
    public static final String UPLOAD_PATH = "uploads";
    public static final String AVATAR_PATH = "uploads/avatars";
    public static final String ARTICLE_IMAGE_PATH = "uploads/articles";
    public static final String TEMP_PATH = "uploads/temp";
    
    // 私有构造函数，防止实例化
    private FileConstants() {
        throw new UnsupportedOperationException("FileConstants class cannot be instantiated");
    }
}
