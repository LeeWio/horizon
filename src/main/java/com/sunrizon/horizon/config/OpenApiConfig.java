package com.sunrizon.horizon.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 配置类
 * 配置 Swagger UI 和 API 文档
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.name:Horizon Blog Platform}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.description:A modern blog platform built with Spring Boot}")
    private String appDescription;

    @Value("${app.contact.name:Horizon Development Team}")
    private String contactName;

    @Value("${app.contact.url:https://github.com/sunrizon/horizon}")
    private String contactUrl;

    @Value("${app.contact.email:dev@sunrizon.com}")
    private String contactEmail;

    @Value("${app.license.name:MIT License}")
    private String licenseName;

    @Value("${app.license.url:https://opensource.org/licenses/MIT}")
    private String licenseUrl;

    @Value("${app.server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(new Server().url(serverUrl).description("Generated server url")))
                .addSecurityItem(securityRequirement())
                .components(securityComponents());
    }

    /**
     * API 基本信息
     */
    private Info apiInfo() {
        return new Info()
                .title(appName + " API")
                .description("# " + appName + " API Documentation\n\n" +
                        appDescription + "\n\n" +
                        "## 核心功能\n" +
                        "- 🔐 **用户认证**: JWT 令牌认证，支持注册、登录\n" +
                        "- 👥 **用户管理**: 用户信息管理、状态控制\n" +
                        "- 🎭 **角色权限**: 基于角色的访问控制 (RBAC)\n" +
                        "- 📝 **文章管理**: 文章 CRUD 操作、状态管理\n" +
                        "- 🔒 **安全控制**: Spring Security + JWT 认证\n\n" +
                        "## 认证说明\n" +
                        "大部分 API 需要 JWT 令牌认证，请先调用登录接口获取 token，然后在请求头中添加：\n" +
                        "```\nAuthorization: Bearer <your_jwt_token>\n```\n")
                .version(appVersion)
                .contact(new Contact()
                        .name(contactName)
                        .email(contactEmail)
                        .url(contactUrl))
                .license(new License()
                        .name(licenseName)
                        .url(licenseUrl));
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
