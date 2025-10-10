package com.sunrizon.horizon.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryVO {
  private String cid;
  private String name;
  private String slug;
  private String description;
  private String parentId; // ID of parent category
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
