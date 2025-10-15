package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.enums.InteractionType;
import com.sunrizon.horizon.pojo.Interaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, String> {

  /**
   * 查找用户对文章的特定类型互动
   */
  Optional<Interaction> findByArticleIdAndUserIdAndType(String articleId, String userId, InteractionType type);

  /**
   * 检查用户是否有某种互动
   */
  boolean existsByArticleIdAndUserIdAndType(String articleId, String userId, InteractionType type);

  /**
   * 统计文章的某种互动数量
   */
  long countByArticleIdAndType(String articleId, InteractionType type);

  /**
   * 获取用户的某种互动列表（分页）
   */
  Page<Interaction> findByUserIdAndType(String userId, InteractionType type, Pageable pageable);

  /**
   * 获取文章的某种互动用户列表（分页）
   */
  Page<Interaction> findByArticleIdAndType(String articleId, InteractionType type, Pageable pageable);

  /**
   * 删除用户对文章的特定互动
   */
  @Modifying
  @Query("DELETE FROM Interaction i WHERE i.articleId = :articleId AND i.userId = :userId AND i.type = :type")
  void deleteByArticleIdAndUserIdAndType(
      @Param("articleId") String articleId,
      @Param("userId") String userId,
      @Param("type") InteractionType type);

  /**
   * 根据metadata查询（用于收藏夹筛选）
   */
  @Query("SELECT i FROM Interaction i WHERE i.userId = :userId AND i.type = :type AND i.metadata LIKE %:keyword%")
  Page<Interaction> findByUserIdAndTypeAndMetadataContaining(
      @Param("userId") String userId,
      @Param("type") InteractionType type,
      @Param("keyword") String keyword,
      Pageable pageable);
}
