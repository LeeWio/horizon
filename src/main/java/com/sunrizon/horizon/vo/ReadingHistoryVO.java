package com.sunrizon.horizon.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReadingHistoryVO {
  private String id;
  private String articleId;
  private String userId;
  private String ipAddress;
  private Integer readTime;
  private LocalDateTime createdAt;
}
