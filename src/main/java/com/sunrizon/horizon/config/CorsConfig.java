package com.sunrizon.horizon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS 跨域配置类
 * 使用专门的配置类处理跨域请求，符合现代 Spring Boot 最佳实践
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    /**
     * 配置 CORS 映射规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(parseOrigins())
                .allowedMethods(parseMethods())
                .allowedHeaders(parseHeaders())
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }

    /**
     * 提供 CORS 配置源给 Spring Security 使用
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 配置允许的源
        if (allowedOrigins.contains("*")) {
            configuration.addAllowedOriginPattern("*");
        } else {
            Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .forEach(configuration::addAllowedOrigin);
        }
        
        // 配置允许的方法
        Arrays.stream(allowedMethods.split(","))
                .map(String::trim)
                .forEach(configuration::addAllowedMethod);
        
        // 配置允许的头部
        if ("*".equals(allowedHeaders)) {
            configuration.addAllowedHeader("*");
        } else {
            Arrays.stream(allowedHeaders.split(","))
                    .map(String::trim)
                    .forEach(configuration::addAllowedHeader);
        }
        
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 解析允许的源
     */
    private String[] parseOrigins() {
        if (allowedOrigins.contains("*")) {
            return new String[]{"*"};
        }
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    /**
     * 解析允许的方法
     */
    private String[] parseMethods() {
        return Arrays.stream(allowedMethods.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    /**
     * 解析允许的头部
     */
    private String[] parseHeaders() {
        if ("*".equals(allowedHeaders)) {
            return new String[]{"*"};
        }
        return Arrays.stream(allowedHeaders.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }
}
