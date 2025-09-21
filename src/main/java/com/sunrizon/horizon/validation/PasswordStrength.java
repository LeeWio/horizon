package com.sunrizon.horizon.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 密码强度验证注解
 * 验证密码是否符合强度要求
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Documented
public @interface PasswordStrength {
    
    String message() default "密码强度不足，必须包含大小写字母、数字和特殊字符，长度至少8位";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 最小长度
     */
    int minLength() default 8;
    
    /**
     * 最大长度
     */
    int maxLength() default 128;
    
    /**
     * 是否必须包含大写字母
     */
    boolean requireUppercase() default true;
    
    /**
     * 是否必须包含小写字母
     */
    boolean requireLowercase() default true;
    
    /**
     * 是否必须包含数字
     */
    boolean requireDigit() default true;
    
    /**
     * 是否必须包含特殊字符
     */
    boolean requireSpecialChar() default true;
}
