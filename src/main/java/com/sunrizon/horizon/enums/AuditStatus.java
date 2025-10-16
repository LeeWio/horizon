package com.sunrizon.horizon.enums;

/**
 * 审核状态枚举
 */
public enum AuditStatus {
  /**
   * 待审核
   */
  PENDING,
  
  /**
   * 审核通过
   */
  APPROVED,
  
  /**
   * 审核拒绝
   */
  REJECTED,
  
  /**
   * 自动通过（系统自动审核）
   */
  AUTO_APPROVED
}
