package com.sunrizon.horizon.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagVO {
    private String tid;
    private String name;
    private String slug;
    private String icon;
    private String color;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}