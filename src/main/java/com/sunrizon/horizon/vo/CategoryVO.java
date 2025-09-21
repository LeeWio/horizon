package com.sunrizon.horizon.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类视图对象
 */
@Getter
@Setter
@Schema(name = "CategoryVO", description = "分类视图对象")
public class CategoryVO {

    @Schema(description = "分类ID")
    private String cid;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类别名")
    private String slug;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "分类颜色")
    private String color;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "是否激活")
    private Boolean isActive;

    @Schema(description = "文章数量")
    private Long articleCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "父分类ID")
    private String parentId;

    @Schema(description = "父分类名称")
    private String parentName;

    @Schema(description = "子分类列表")
    private List<CategoryVO> children;
}
