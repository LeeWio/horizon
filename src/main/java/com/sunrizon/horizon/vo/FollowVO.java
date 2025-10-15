package com.sunrizon.horizon.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Follow View Object
 */
@Data
public class FollowVO {

  private String id;
  private String followerId;
  private String followingId;
  private LocalDateTime createdAt;

  // 扩展信息：用户基本信息
  private String username;
  private String avatar;
}
