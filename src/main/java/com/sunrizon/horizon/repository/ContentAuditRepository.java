package com.sunrizon.horizon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.enums.AuditStatus;
import com.sunrizon.horizon.enums.ContentType;
import com.sunrizon.horizon.pojo.ContentAudit;

/**
 * 内容审核记录数据访问层
 */
@Repository
public interface ContentAuditRepository extends JpaRepository<ContentAudit, String> {

  /**
   * 根据内容类型和内容ID查询最新的审核记录
   */
  Optional<ContentAudit> findFirstByContentTypeAndContentIdOrderByCreatedAtDesc(
      ContentType contentType, String contentId);

  /**
   * 根据内容类型和内容ID查询所有审核记录
   */
  List<ContentAudit> findByContentTypeAndContentIdOrderByCreatedAtDesc(
      ContentType contentType, String contentId);

  /**
   * 根据审核状态分页查询
   */
  Page<ContentAudit> findByStatus(AuditStatus status, Pageable pageable);

  /**
   * 根据内容类型和审核状态分页查询
   */
  Page<ContentAudit> findByContentTypeAndStatus(
      ContentType contentType, AuditStatus status, Pageable pageable);

  /**
   * 根据提交者ID分页查询
   */
  Page<ContentAudit> findBySubmitterId(String submitterId, Pageable pageable);

  /**
   * 根据审核员ID分页查询
   */
  Page<ContentAudit> findByAuditorId(String auditorId, Pageable pageable);

  /**
   * 统计待审核数量
   */
  @Query("SELECT COUNT(c) FROM ContentAudit c WHERE c.status = :status")
  long countByStatus(@Param("status") AuditStatus status);

  /**
   * 统计各状态的审核数量
   */
  @Query("SELECT c.status, COUNT(c) FROM ContentAudit c GROUP BY c.status")
  List<Object[]> countGroupByStatus();

  /**
   * 查询指定时间范围内的审核记录
   */
  @Query("SELECT c FROM ContentAudit c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate ORDER BY c.createdAt DESC")
  Page<ContentAudit> findByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable);

  /**
   * 查询包含敏感词的审核记录
   */
  @Query("SELECT c FROM ContentAudit c WHERE c.sensitiveWords IS NOT NULL AND c.sensitiveWords != '' ORDER BY c.createdAt DESC")
  Page<ContentAudit> findWithSensitiveWords(Pageable pageable);

  /**
   * 查询自动审核的记录
   */
  Page<ContentAudit> findByIsAutoAudit(Boolean isAutoAudit, Pageable pageable);
}
