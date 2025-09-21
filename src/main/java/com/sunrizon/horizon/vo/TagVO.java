package com.sunrizon.horizon.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 标签视图对象
 */
@Getter
@Setter
@Schema(name = "TagVO", description = "标签视图对象")
public class TagVO {

    @Schema(description = "标签ID")
    private String tid;

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "标签别名")
    private String slug;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "标签颜色")
    private String color;

    @Schema(description = "文章数量")
    private Long articleCount;

    @Schema(description = "是否激活")
    private Boolean isActive;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
