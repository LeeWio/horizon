package com.sunrizon.horizon.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sunrizon.horizon.enums.AuditStatus;
import com.sunrizon.horizon.enums.ContentType;
import com.sunrizon.horizon.pojo.ContentAudit;
import com.sunrizon.horizon.service.IAuditService;
import com.sunrizon.horizon.utils.ResultResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 内容审核控制器
 */
@RestController
@RequestMapping("/api/audit")
@Slf4j
@Tag(name = "Content Audit", description = "Content audit management APIs")
public class AuditController {

  @Resource
  private IAuditService auditService;

  /**
   * 获取审核记录详情
   */
  @GetMapping("/{auditId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Get audit record details")
  public ResultResponse<ContentAudit> getAuditRecord(
      @Parameter(description = "Audit ID") @PathVariable String auditId) {
    return auditService.getAuditRecord(auditId);
  }

  /**
   * 获取内容的审核历史
   */
  @GetMapping("/history/{contentType}/{contentId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Get content audit history")
  public ResultResponse<List<ContentAudit>> getAuditHistory(
      @Parameter(description = "Content type") @PathVariable ContentType contentType,
      @Parameter(description = "Content ID") @PathVariable String contentId) {
    return auditService.getAuditHistory(contentType, contentId);
  }

  /**
   * 获取待审核列表
   */
  @GetMapping("/pending")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Get pending audits")
  public ResultResponse<Page<ContentAudit>> getPendingAudits(
      @Parameter(description = "Content type (optional)") @RequestParam(required = false) ContentType contentType,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return auditService.getPendingAudits(contentType, pageable);
  }

  /**
   * 审核内容
   */
  @PostMapping("/{auditId}/review")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Review content")
  public ResultResponse<ContentAudit> reviewContent(
      @Parameter(description = "Audit ID") @PathVariable String auditId,
      @RequestBody Map<String, Object> request) {
    AuditStatus status = AuditStatus.valueOf((String) request.get("status"));
    String comment = (String) request.get("comment");
    return auditService.reviewContent(auditId, status, comment);
  }

  /**
   * 批量审核
   */
  @PostMapping("/batch-review")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Batch review content")
  public ResultResponse<Integer> batchReview(@RequestBody Map<String, Object> request) {
    @SuppressWarnings("unchecked")
    List<String> auditIds = (List<String>) request.get("auditIds");
    AuditStatus status = AuditStatus.valueOf((String) request.get("status"));
    String comment = (String) request.get("comment");
    return auditService.batchReview(auditIds, status, comment);
  }

  /**
   * 获取审核统计数据
   */
  @GetMapping("/statistics")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Get audit statistics")
  public ResultResponse<Map<String, Object>> getStatistics() {
    return auditService.getAuditStatistics();
  }

  /**
   * 获取指定时间范围内的审核记录
   */
  @GetMapping("/date-range")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  @Operation(summary = "Get audits by date range")
  public ResultResponse<Page<ContentAudit>> getAuditsByDateRange(
      @Parameter(description = "Start date") 
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @Parameter(description = "End date") 
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return auditService.getAuditsByDateRange(startDate, endDate, pageable);
  }
}
