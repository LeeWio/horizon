package com.sunrizon.horizon.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 互动请求 DTO
 */
@Setter
@Getter
public class InteractionRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private String articleId;

  private String metadata; // 可选，如收藏夹名称
}
