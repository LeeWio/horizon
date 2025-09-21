package com.sunrizon.horizon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建分类请求DTO
 */
@Getter
@Setter
@Schema(name = "CreateCategoryRequest", description = "创建分类请求")
public class CreateCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(min = 1, max = 100, message = "分类名称长度必须在1-100个字符之间")
    @Schema(description = "分类名称", example = "技术分享", required = true)
    private String name;

    @Size(max = 150, message = "分类别名长度不能超过150个字符")
    @Schema(description = "分类别名", example = "tech-share")
    private String slug;

    @Size(max = 500, message = "分类描述长度不能超过500个字符")
    @Schema(description = "分类描述", example = "分享技术相关的文章和心得")
    private String description;

    @Size(max = 7, message = "颜色代码格式不正确")
    @Schema(description = "分类颜色", example = "#FF5722")
    private String color;

    @Size(max = 100, message = "图标长度不能超过100个字符")
    @Schema(description = "分类图标", example = "fas fa-code")
    private String icon;

    @Schema(description = "排序顺序", example = "0")
    private Integer sortOrder = 0;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive = true;

    @Schema(description = "父分类ID", example = "parent-category-id")
    private String parentId;
}
