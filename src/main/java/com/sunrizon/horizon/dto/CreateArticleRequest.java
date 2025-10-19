package com.sunrizon.horizon.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.sunrizon.horizon.enums.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateArticleRequest {

  @NotBlank(message = "Article title is required")
  @Size(max = 100, message = "Article title must not exceed 100 characters")
  private String title;

  @Size(max = 150, message = "Article slug must not exceed 150 characters")
  private String slug;

  @Size(max = 500, message = "Article summary must not exceed 500 characters")
  private String summary;

  @NotBlank(message = "Article content is required")
  private String content;

  private String coverImage;

  private ArticleStatus status = ArticleStatus.DRAFT;
  
  private LocalDateTime publishTime;

  @NotBlank(message = "Author ID is required")
  private String authorId;

  private List<String> categoryIds; // IDs of associated categories

  private List<String> seriesIds; // IDs of associated series

  private List<String> tagIds; // IDs of associated tags
}
