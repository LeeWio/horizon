package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.SeriesStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeriesVO {
    private String sid;
    private String name;
    private String slug;
    private String description;
    private String coverImage;
    private SeriesStatus status;
    private String authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer articleCount; // Number of articles in this series
}