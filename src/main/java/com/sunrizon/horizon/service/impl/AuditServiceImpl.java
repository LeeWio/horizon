package com.sunrizon.horizon.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunrizon.horizon.enums.AuditReason;
import com.sunrizon.horizon.enums.AuditStatus;
import com.sunrizon.horizon.enums.ContentType;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.ContentAudit;
import com.sunrizon.horizon.repository.ContentAuditRepository;
import com.sunrizon.horizon.service.IAuditService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 内容审核服务实现类
 */
@Service
@Slf4j
public class AuditServiceImpl implements IAuditService {

  @Resource
  private ContentAuditRepository contentAuditRepository;

  @Resource
  private SecurityContextUtil securityContextUtil;

  @Override
  @Transactional
  public ResultResponse<ContentAudit> submitForAudit(
      ContentType contentType,
      String contentId,
      String submitterId,
      String contentSnapshot,
      AuditReason reason,
      String sensitiveWords) {

    ContentAudit audit = new ContentAudit();
    audit.setContentType(contentType);
    audit.setContentId(contentId);
    audit.setSubmitterId(submitterId);
    audit.setContentSnapshot(contentSnapshot);
    audit.setReason(reason);
    audit.setSensitiveWords(sensitiveWords);
    audit.setStatus(AuditStatus.PENDING);
    audit.setIsAutoAudit(false);

    ContentAudit saved = contentAuditRepository.save(audit);
    log.info("Content submitted for audit: type={}, id={}, reason={}", contentType, contentId, reason);

    return ResultResponse.success(saved);
  }

  @Override
  @Transactional
  public ResultResponse<ContentAudit> reviewContent(String auditId, AuditStatus status, String comment) {
    if (StrUtil.isBlank(auditId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Audit ID cannot be empty");
    }

    Optional<ContentAudit> auditOpt = contentAuditRepository.findById(auditId);
    if (auditOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.NOT_FOUND, "Audit record not found");
    }

    ContentAudit audit = auditOpt.get();
    
    // Check if already reviewed
    if (audit.getStatus() != AuditStatus.PENDING) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Audit already reviewed");
    }

    // Get current user as auditor
    Optional<String> auditorIdOpt = securityContextUtil.getCurrentUserId();
    if (auditorIdOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.UNAUTHORIZED, "User not authenticated");
    }

    audit.setStatus(status);
    audit.setComment(comment);
    audit.setAuditorId(auditorIdOpt.get());
    audit.setAuditedAt(LocalDateTime.now());

    ContentAudit updated = contentAuditRepository.save(audit);
    log.info("Content reviewed: id={}, status={}, auditor={}", auditId, status, auditorIdOpt.get());

    return ResultResponse.success(updated);
  }

  @Override
  public ResultResponse<ContentAudit> getAuditRecord(String auditId) {
    if (StrUtil.isBlank(auditId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Audit ID cannot be empty");
    }

    Optional<ContentAudit> auditOpt = contentAuditRepository.findById(auditId);
    if (auditOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.NOT_FOUND, "Audit record not found");
    }

    return ResultResponse.success(auditOpt.get());
  }

  @Override
  public ResultResponse<List<ContentAudit>> getAuditHistory(ContentType contentType, String contentId) {
    List<ContentAudit> history = contentAuditRepository
        .findByContentTypeAndContentIdOrderByCreatedAtDesc(contentType, contentId);
    
    return ResultResponse.success(history);
  }

  @Override
  public ResultResponse<Page<ContentAudit>> getPendingAudits(ContentType contentType, Pageable pageable) {
    Page<ContentAudit> page;
    
    if (contentType != null) {
      page = contentAuditRepository.findByContentTypeAndStatus(contentType, AuditStatus.PENDING, pageable);
    } else {
      page = contentAuditRepository.findByStatus(AuditStatus.PENDING, pageable);
    }

    return ResultResponse.success(page);
  }

  @Override
  public ResultResponse<Map<String, Object>> getAuditStatistics() {
    Map<String, Object> stats = new HashMap<>();

    // Count by status
    List<Object[]> statusCounts = contentAuditRepository.countGroupByStatus();
    Map<String, Long> statusMap = new HashMap<>();
    for (Object[] row : statusCounts) {
      AuditStatus status = (AuditStatus) row[0];
      Long count = (Long) row[1];
      statusMap.put(status.name(), count);
    }
    stats.put("byStatus", statusMap);

    // Pending count
    long pendingCount = contentAuditRepository.countByStatus(AuditStatus.PENDING);
    stats.put("pending", pendingCount);

    return ResultResponse.success(stats);
  }

  @Override
  public ResultResponse<Page<ContentAudit>> getAuditsByDateRange(
      LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
    
    Page<ContentAudit> page = contentAuditRepository.findByDateRange(startDate, endDate, pageable);
    return ResultResponse.success(page);
  }

  @Override
  @Transactional
  public ResultResponse<Integer> batchReview(List<String> auditIds, AuditStatus status, String comment) {
    if (auditIds == null || auditIds.isEmpty()) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Audit IDs cannot be empty");
    }

    Optional<String> auditorIdOpt = securityContextUtil.getCurrentUserId();
    if (auditorIdOpt.isEmpty()) {
      return ResultResponse.error(ResponseCode.UNAUTHORIZED, "User not authenticated");
    }

    int count = 0;
    for (String auditId : auditIds) {
      Optional<ContentAudit> auditOpt = contentAuditRepository.findById(auditId);
      if (auditOpt.isPresent() && auditOpt.get().getStatus() == AuditStatus.PENDING) {
        ContentAudit audit = auditOpt.get();
        audit.setStatus(status);
        audit.setComment(comment);
        audit.setAuditorId(auditorIdOpt.get());
        audit.setAuditedAt(LocalDateTime.now());
        contentAuditRepository.save(audit);
        count++;
      }
    }

    log.info("Batch reviewed: count={}, status={}", count, status);
    return ResultResponse.success(count);
  }
}
