package com.sunrizon.horizon.dto;

import lombok.Data;

@Data
public class RecordReadingRequest {

  private String articleId;

  private String userId; // 可选，未登录用户为空

  private Integer readTime; // 阅读时长（秒），可选
}
