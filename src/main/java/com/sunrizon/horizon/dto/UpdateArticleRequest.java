package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.enums.ArticleStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateArticleRequest {

  @Size(max = 100, message = "文章标题不能超过100个字符")
  private String title;

  @Size(max = 150, message = "文章slug不能超过150个字符")
  private String slug;

  @Size(max = 500, message = "文章摘要不能超过500个字符")
  private String summary;

  private String content;

  private String coverImage;

  private ArticleStatus status;

  private Boolean isFeatured;

  private List<String> categoryIds;

  private List<String> seriesIds;

  private List<String> tagIds;
}
