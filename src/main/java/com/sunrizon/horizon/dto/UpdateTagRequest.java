package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTagRequest {

  @Size(max = 50, message = "Tag name must not exceed 50 characters")
  private String name;

  @Size(max = 100, message = "Tag slug must not exceed 100 characters")
  private String slug;

  private String icon;

  @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
  private String color;

  @Size(max = 200, message = "Tag description must not exceed 200 characters")
  private String description;
}