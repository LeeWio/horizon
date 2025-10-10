package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.ColumnStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ColumnVO {
    private String cid;
    private String name;
    private String slug;
    private String description;
    private String coverImage;
    private ColumnStatus status;
    private String authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer articleCount; // Number of articles in this column
}