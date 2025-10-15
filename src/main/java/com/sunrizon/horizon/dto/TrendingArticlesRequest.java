package com.sunrizon.horizon.dto;

import lombok.Data;

/**
 * DTO for trending articles query parameters
 */
@Data
public class TrendingArticlesRequest {

  /**
   * Ranking type: VIEW, LIKE, FAVORITE
   */
  private String type = "VIEW";

  /**
   * Time range: DAY, WEEK, MONTH, ALL
   */
  private String timeRange = "WEEK";

  /**
   * Page size (default 10)
   */
  private Integer size = 10;

  /**
   * Page number (default 0)
   */
  private Integer page = 0;
}
