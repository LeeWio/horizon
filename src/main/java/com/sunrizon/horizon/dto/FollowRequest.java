package com.sunrizon.horizon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Follow Request DTO
 */
@Data
public class FollowRequest {

  @NotBlank(message = "被关注用户ID不能为空")
  private String followingId;
}
