package com.sunrizon.horizon.constants;

/**
 * 用户相关常量
 */
public class UserConstants {
    
    // 用户名相关
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,50}$";
    
    // 密码相关
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    // 邮箱相关
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    // 头像相关
    public static final int AVATAR_MAX_LENGTH = 255;
    public static final String DEFAULT_AVATAR = "/static/images/default-avatar.png";
    public static final String AVATAR_PATH = "uploads/avatars";
    
    // 状态相关
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_BANNED = "BANNED";
    public static final String STATUS_DELETED = "DELETED";
    
    // 角色相关
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_EDITOR = "EDITOR";
    public static final String ROLE_USER = "USER";
    
    // 私有构造函数，防止实例化
    private UserConstants() {
        throw new UnsupportedOperationException("UserConstants class cannot be instantiated");
    }
}
