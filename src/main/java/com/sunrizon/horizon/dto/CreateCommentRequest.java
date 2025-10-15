package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {

  @NotBlank(message = "文章ID不能为空")
  private String articleId;

  @NotBlank(message = "用户ID不能为空")
  private String userId;

  @NotBlank(message = "评论内容不能为空")
  private String content;

  private String parentId; // 父评论ID（可选，用于回复评论）
}
