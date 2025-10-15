package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.ReadingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, String> {

  /**
   * 根据文章ID查询阅读历史
   *
   * @param articleId 文章ID
   * @return 阅读历史列表
   */
  List<ReadingHistory> findByArticleId(String articleId);

  /**
   * 根据用户ID查询阅读历史
   *
   * @param userId 用户ID
   * @return 阅读历史列表
   */
  List<ReadingHistory> findByUserId(String userId);

  /**
   * 根据用户ID分页查询阅读历史
   *
   * @param userId   用户ID
   * @param pageable 分页参数
   * @return 分页阅读历史
   */
  Page<ReadingHistory> findByUserId(String userId, Pageable pageable);

  /**
   * 统计文章的总浏览量（PV）
   *
   * @param articleId 文章ID
   * @return 浏览量
   */
  @Query("SELECT COUNT(r) FROM ReadingHistory r WHERE r.articleId = :articleId")
  Long countByArticleId(@Param("articleId") String articleId);

  /**
   * 统计文章的独立访客数（UV）- 基于用户ID
   *
   * @param articleId 文章ID
   * @return 独立访客数
   */
  @Query("SELECT COUNT(DISTINCT r.userId) FROM ReadingHistory r WHERE r.articleId = :articleId AND r.userId IS NOT NULL")
  Long countUniqueUsersByArticleId(@Param("articleId") String articleId);

  /**
   * 统计文章的独立访客数（UV）- 基于IP地址
   *
   * @param articleId 文章ID
   * @return 独立访客数
   */
  @Query("SELECT COUNT(DISTINCT r.ipAddress) FROM ReadingHistory r WHERE r.articleId = :articleId AND r.ipAddress IS NOT NULL")
  Long countUniqueIpsByArticleId(@Param("articleId") String articleId);

  /**
   * 查询指定时间范围内的阅读历史
   *
   * @param articleId 文章ID
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @return 阅读历史列表
   */
  @Query("SELECT r FROM ReadingHistory r WHERE r.articleId = :articleId AND r.createdAt BETWEEN :startTime AND :endTime")
  List<ReadingHistory> findByArticleIdAndTimeRange(
      @Param("articleId") String articleId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);
}
