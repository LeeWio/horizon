package com.sunrizon.horizon.constants;

/**
 * 评论相关常量
 */
public class CommentConstants {
    
    // 评论内容相关
    public static final int COMMENT_CONTENT_MIN_LENGTH = 1;
    public static final int COMMENT_CONTENT_MAX_LENGTH = 2000;
    
    // 评论作者相关
    public static final int COMMENT_AUTHOR_NAME_MAX_LENGTH = 100;
    public static final int COMMENT_AUTHOR_EMAIL_MAX_LENGTH = 100;
    public static final int COMMENT_AUTHOR_WEBSITE_MAX_LENGTH = 255;
    
    // 私有构造函数，防止实例化
    private CommentConstants() {
        throw new UnsupportedOperationException("CommentConstants class cannot be instantiated");
    }
}
