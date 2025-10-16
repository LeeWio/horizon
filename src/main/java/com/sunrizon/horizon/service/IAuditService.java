package com.sunrizon.horizon.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sunrizon.horizon.enums.AuditReason;
import com.sunrizon.horizon.enums.AuditStatus;
import com.sunrizon.horizon.enums.ContentType;
import com.sunrizon.horizon.pojo.ContentAudit;
import com.sunrizon.horizon.utils.ResultResponse;

/**
 * 内容审核服务接口
 */
public interface IAuditService {

  /**
   * 提交内容审核
   *
   * @param contentType 内容类型
   * @param contentId 内容ID
   * @param submitterId 提交者ID
   * @param contentSnapshot 内容快照
   * @param reason 审核原因
   * @param sensitiveWords 敏感词列表
   * @return 审核记录
   */
  ResultResponse<ContentAudit> submitForAudit(
      ContentType contentType,
      String contentId,
      String submitterId,
      String contentSnapshot,
      AuditReason reason,
      String sensitiveWords);

  /**
   * 审核内容（通过/拒绝）
   *
   * @param auditId 审核记录ID
   * @param status 审核状态
   * @param comment 审核备注
   * @return 审核结果
   */
  ResultResponse<ContentAudit> reviewContent(String auditId, AuditStatus status, String comment);

  /**
   * 获取审核记录详情
   *
   * @param auditId 审核记录ID
   * @return 审核记录
   */
  ResultResponse<ContentAudit> getAuditRecord(String auditId);

  /**
   * 获取内容的审核历史
   *
   * @param contentType 内容类型
   * @param contentId 内容ID
   * @return 审核历史列表
   */
  ResultResponse<java.util.List<ContentAudit>> getAuditHistory(ContentType contentType, String contentId);

  /**
   * 获取待审核列表
   *
   * @param contentType 内容类型（可选）
   * @param pageable 分页参数
   * @return 待审核列表
   */
  ResultResponse<Page<ContentAudit>> getPendingAudits(ContentType contentType, Pageable pageable);

  /**
   * 获取审核统计数据
   *
   * @return 统计数据
   */
  ResultResponse<Map<String, Object>> getAuditStatistics();

  /**
   * 获取指定时间范围内的审核记录
   *
   * @param startDate 开始时间
   * @param endDate 结束时间
   * @param pageable 分页参数
   * @return 审核记录
   */
  ResultResponse<Page<ContentAudit>> getAuditsByDateRange(
      LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

  /**
   * 批量审核
   *
   * @param auditIds 审核记录ID列表
   * @param status 审核状态
   * @param comment 审核备注
   * @return 批量审核结果
   */
  ResultResponse<Integer> batchReview(
      java.util.List<String> auditIds, AuditStatus status, String comment);
}
