package com.sunrizon.horizon.constants;

/**
 * 分类相关常量
 */
public class CategoryConstants {
    
    // 分类内容相关
    public static final int CATEGORY_NAME_MIN_LENGTH = 1;
    public static final int CATEGORY_NAME_MAX_LENGTH = 100;
    public static final int CATEGORY_SLUG_MAX_LENGTH = 100;
    public static final int CATEGORY_DESCRIPTION_MAX_LENGTH = 500;
    public static final String SLUG_REGEX = "^[a-z0-9-]+$";
    
    // 私有构造函数，防止实例化
    private CategoryConstants() {
        throw new UnsupportedOperationException("CategoryConstants class cannot be instantiated");
    }
}
