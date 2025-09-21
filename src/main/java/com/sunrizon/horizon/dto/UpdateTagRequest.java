package com.sunrizon.horizon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新标签请求DTO
 */
@Getter
@Setter
@Schema(name = "UpdateTagRequest", description = "更新标签请求")
public class UpdateTagRequest {

    @Size(min = 1, max = 50, message = "标签名称长度必须在1-50个字符之间")
    @Schema(description = "标签名称", example = "Spring Boot")
    private String name;

    @Size(max = 100, message = "标签别名长度不能超过100个字符")
    @Schema(description = "标签别名", example = "spring-boot")
    private String slug;

    @Size(max = 300, message = "标签描述长度不能超过300个字符")
    @Schema(description = "标签描述", example = "Spring Boot 相关技术")
    private String description;

    @Size(max = 7, message = "颜色代码格式不正确")
    @Schema(description = "标签颜色", example = "#4CAF50")
    private String color;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;
}
