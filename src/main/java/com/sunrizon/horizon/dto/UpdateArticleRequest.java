package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.enums.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 更新文章请求DTO
 */
@Getter
@Setter
@Schema(name = "UpdateArticleRequest", description = "更新文章请求")
public class UpdateArticleRequest {

    @Size(min = 1, max = 200, message = "文章标题长度必须在1-200个字符之间")
    @Schema(description = "文章标题", example = "Spring Boot 最佳实践")
    private String title;

    @Size(max = 200, message = "文章别名长度不能超过200个字符")
    @Schema(description = "文章别名", example = "spring-boot-best-practices")
    private String slug;

    @Size(max = 1000, message = "文章摘要长度不能超过1000个字符")
    @Schema(description = "文章摘要", example = "本文介绍了Spring Boot开发中的最佳实践...")
    private String summary;

    @Schema(description = "文章内容")
    private String content;

    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "文章状态", example = "PUBLISHED")
    private ArticleStatus status;

    @Schema(description = "是否精选", example = "true")
    private Boolean isFeatured;

    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;

    @Schema(description = "分类ID列表", example = "[\"category-id-1\", \"category-id-2\"]")
    private List<String> categoryIds;

    @Schema(description = "标签ID列表", example = "[\"tag-id-1\", \"tag-id-2\"]")
    private List<String> tagIds;
}
