package com.sunrizon.horizon.enums;

/**
 * 文件状态枚举
 */
public enum FileStatus {
  /**
   * 活跃 - 文件正常可用
   */
  ACTIVE,

  /**
   * 删除 - 文件已标记删除（软删除）
   */
  DELETED,

  /**
   * 临时 - 临时上传的文件
   */
  TEMPORARY,

  /**
   * 待处理 - 文件上传完成但还在处理中（如转换、压缩等）
   */
  PENDING
}
