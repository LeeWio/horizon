package com.sunrizon.horizon.constants;

/**
 * 角色相关常量
 */
public class RoleConstants {
    
    // 角色内容相关
    public static final int ROLE_NAME_MIN_LENGTH = 1;
    public static final int ROLE_NAME_MAX_LENGTH = 50;
    public static final int ROLE_DESCRIPTION_MAX_LENGTH = 255;
    
    // 角色名称
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_EDITOR = "EDITOR";
    public static final String ROLE_USER = "USER";
    
    // 私有构造函数，防止实例化
    private RoleConstants() {
        throw new UnsupportedOperationException("RoleConstants class cannot be instantiated");
    }
}
