package com.sunrizon.horizon.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码测试工具类
 */
public class PasswordTest {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Wei.Li.Laba00";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt加密后: " + hashedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("密码验证结果: " + matches);
        
        // 验证我们之前使用的密码
        String oldPassword = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        boolean oldMatches = encoder.matches(password, oldPassword);
        System.out.println("旧密码验证结果: " + oldMatches);
    }
}
