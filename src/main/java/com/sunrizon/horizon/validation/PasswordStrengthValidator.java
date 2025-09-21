package com.sunrizon.horizon.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 密码强度验证器
 */
public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {
    
    private int minLength;
    private int maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;
    
    @Override
    public void initialize(PasswordStrength constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // 让 @NotNull 或 @NotBlank 处理空值
        }
        
        // 检查长度
        if (password.length() < minLength || password.length() > maxLength) {
            return false;
        }
        
        // 检查大写字母
        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // 检查小写字母
        if (requireLowercase && !password.matches(".*[a-z].*")) {
            return false;
        }
        
        // 检查数字
        if (requireDigit && !password.matches(".*\\d.*")) {
            return false;
        }
        
        // 检查特殊字符
        if (requireSpecialChar && !password.matches(".*[@$!%*?&].*")) {
            return false;
        }
        
        return true;
    }
}
