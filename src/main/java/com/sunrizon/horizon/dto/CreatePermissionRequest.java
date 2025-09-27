package com.sunrizon.horizon.dto;

import com.sunrizon.horizon.enums.PermissionType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "CreatePermissionRequest", description = "创建权限请求")
public class CreatePermissionRequest {

  @NotNull(message = "权限名称不能为空")
  @Schema(description = "权限名称，例如：ARTICLE_CREATE", example = "ARTICLE_CREATE", required = true)
  private PermissionType name;

  @Size(max = 255, message = "权限描述长度不能超过255个字符")
  @Schema(description = "权限描述", example = "创建文章权限", required = true)
  private String description;
}
