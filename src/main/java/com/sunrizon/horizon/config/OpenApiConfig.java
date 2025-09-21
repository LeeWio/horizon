package com.sunrizon.horizon.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 * 配置 Swagger UI 和 API 文档
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement())
                .components(securityComponents());
    }

    /**
     * API 基本信息
     */
    private Info apiInfo() {
        return new Info()
                .title("Horizon Blog Platform API")
                .description("""
                        # Horizon Blog Platform API Documentation
                        
                        这是一个现代化的博客平台后端 API 文档，提供以下功能：
                        
                        ## 核心功能
                        - 🔐 **用户认证**: JWT 令牌认证，支持注册、登录
                        - 👥 **用户管理**: 用户信息管理、状态控制
                        - 🎭 **角色权限**: 基于角色的访问控制 (RBAC)
                        - 📝 **文章管理**: 文章 CRUD 操作、状态管理
                        - 🔒 **安全控制**: Spring Security + JWT 认证
                        
                        ## 认证说明
                        大部分 API 需要 JWT 令牌认证，请先调用登录接口获取 token，然后在请求头中添加：
                        ```
                        Authorization: Bearer <your_jwt_token>
                        ```
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Horizon Development Team")
                        .email("dev@sunrizon.com")
                        .url("https://github.com/sunrizon/horizon"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * 安全认证要求
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Bearer Authentication");
    }

    /**
     * 安全组件配置
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("请输入 JWT 令牌")
                );
    }
}
