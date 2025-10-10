package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {
    private String aid;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImage;
    private ArticleStatus status;
    private String authorId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryVO> categories;
    private List<SeriesVO> series;
    private List<TagVO> tags;
}