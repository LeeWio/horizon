package com.sunrizon.horizon.constants;

/**
 * 标签相关常量
 */
public class TagConstants {
    
    // 标签内容相关
    public static final int TAG_NAME_MIN_LENGTH = 1;
    public static final int TAG_NAME_MAX_LENGTH = 100;
    public static final int TAG_SLUG_MAX_LENGTH = 100;
    public static final int TAG_DESCRIPTION_MAX_LENGTH = 500;
    public static final String SLUG_REGEX = "^[a-z0-9-]+$";
    
    // 私有构造函数，防止实例化
    private TagConstants() {
        throw new UnsupportedOperationException("TagConstants class cannot be instantiated");
    }
}
