package com.sunrizon.horizon.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章统计视图对象
 */
@Getter
@Setter
@Schema(name = "ArticleStatsVO", description = "文章统计视图对象")
public class ArticleStatsVO {

    @Schema(description = "浏览量")
    private Long viewCount = 0L;

    @Schema(description = "点赞数")
    private Long likeCount = 0L;

    @Schema(description = "评论数")
    private Long commentCount = 0L;

    @Schema(description = "分享数")
    private Long shareCount = 0L;

    @Schema(description = "收藏数")
    private Long favoriteCount = 0L;
}
