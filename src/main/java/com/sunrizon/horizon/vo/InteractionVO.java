package com.sunrizon.horizon.vo;

import com.sunrizon.horizon.enums.InteractionType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 互动 View Object
 */
@Setter
@Getter
public class InteractionVO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String articleId;

  private String userId;

  private InteractionType type;

  private String metadata;

  private LocalDateTime createdAt;
}
