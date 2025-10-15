package com.sunrizon.horizon.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentVO {
  private String cid;
  private String articleId;
  private String userId;
  private String content;
  private String parentId;
  private Integer likesCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
