package com.sunrizon.horizon.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全上下文工具类
 * 用于获取当前登录用户信息
 */
public class SecurityContextUtil {

    /**
     * 获取当前登录用户的用户名
     * @return 用户名，如果未登录则返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户的ID
     * 注意：这里假设用户名就是用户ID，实际项目中可能需要从数据库查询
     * @return 用户ID，如果未登录则返回null
     */
    public static String getCurrentUserId() {
        return getCurrentUsername();
    }

    /**
     * 检查当前用户是否已登录
     * @return true如果已登录，false如果未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 获取当前认证对象
     * @return 认证对象，如果未登录则返回null
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
