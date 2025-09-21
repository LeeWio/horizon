package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象
 */
@Getter
@Setter
@Schema(name = "ArticleVO", description = "文章视图对象")
public class ArticleVO {

    @Schema(description = "文章ID")
    private String aid;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章别名")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "文章状态")
    private ArticleStatus status;

    @Schema(description = "作者ID")
    private String authorId;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "是否精选")
    private Boolean isFeatured;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "创建者")
    private String createdBy;

    @Schema(description = "更新者")
    private String updatedBy;

    @Schema(description = "文章统计信息")
    private ArticleStatsVO stats;

    @Schema(description = "分类列表")
    private List<CategoryVO> categories;

    @Schema(description = "标签列表")
    private List<TagVO> tags;
}
