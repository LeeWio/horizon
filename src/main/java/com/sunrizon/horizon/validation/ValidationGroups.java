package com.sunrizon.horizon.validation;

/**
 * 验证分组
 * 用于在不同场景下使用不同的验证规则
 */
public class ValidationGroups {
    
    /**
     * 创建分组
     * 用于创建操作时的验证
     */
    public interface Create {}
    
    /**
     * 更新分组
     * 用于更新操作时的验证
     */
    public interface Update {}
    
    /**
     * 登录分组
     * 用于登录操作时的验证
     */
    public interface Login {}
    
    /**
     * 注册分组
     * 用于注册操作时的验证
     */
    public interface Register {}
    
    /**
     * 密码重置分组
     * 用于密码重置操作时的验证
     */
    public interface PasswordReset {}
    
    /**
     * 用户信息更新分组
     * 用于用户信息更新操作时的验证
     */
    public interface UserUpdate {}
    
    /**
     * 文章创建分组
     * 用于文章创建操作时的验证
     */
    public interface ArticleCreate {}
    
    /**
     * 文章更新分组
     * 用于文章更新操作时的验证
     */
    public interface ArticleUpdate {}
    
    /**
     * 评论创建分组
     * 用于评论创建操作时的验证
     */
    public interface CommentCreate {}
    
    /**
     * 文件上传分组
     * 用于文件上传操作时的验证
     */
    public interface FileUpload {}
}
