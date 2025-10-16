package com.sunrizon.horizon.enums;

/**
 * 审核原因枚举
 */
public enum AuditReason {
  /**
   * 包含敏感词
   */
  SENSITIVE_WORD,
  
  /**
   * 垃圾内容
   */
  SPAM,
  
  /**
   * 广告内容
   */
  ADVERTISEMENT,
  
  /**
   * 色情内容
   */
  PORNOGRAPHY,
  
  /**
   * 暴力内容
   */
  VIOLENCE,
  
  /**
   * 政治敏感
   */
  POLITICAL,
  
  /**
   * 人工举报
   */
  USER_REPORT,
  
  /**
   * 其他
   */
  OTHER
}
