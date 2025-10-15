package com.sunrizon.horizon.service;

import com.sunrizon.horizon.enums.InteractionType;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.InteractionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interaction Service Interface
 */
public interface IInteractionService {

  /**
   * 添加互动（点赞/收藏/分享）
   */
  ResultResponse<InteractionVO> addInteraction(String userId, String articleId,
      InteractionType type, String metadata);

  /**
   * 取消互动
   */
  ResultResponse<String> removeInteraction(String userId, String articleId, InteractionType type);

  /**
   * 检查是否存在互动
   */
  ResultResponse<Boolean> checkInteraction(String userId, String articleId, InteractionType type);

  /**
   * 获取文章的互动数量
   */
  ResultResponse<Long> getInteractionCount(String articleId, InteractionType type);

  /**
   * 获取用户的互动列表（分页）
   */
  ResultResponse<Page<InteractionVO>> getUserInteractions(String userId, InteractionType type,
      Pageable pageable);

  /**
   * 获取文章的互动用户列表（分页）
   */
  ResultResponse<Page<InteractionVO>> getArticleInteractions(String articleId, InteractionType type,
      Pageable pageable);

  /**
   * 按metadata筛选用户互动（用于收藏夹）
   */
  ResultResponse<Page<InteractionVO>> getUserInteractionsByMetadata(String userId,
      InteractionType type, String keyword, Pageable pageable);

  // ==================== 通用互动方法（支持文章和评论） ====================

  /**
   * 添加通用互动（支持文章和评论）
   *
   * @param userId     用户ID
   * @param targetId   目标ID（文章ID或评论ID）
   * @param targetType 目标类型（ARTICLE或COMMENT）
   * @param type       互动类型（LIKE/FAVORITE/SHARE）
   * @param metadata   元数据（可选）
   * @return 互动VO
   */
  ResultResponse<InteractionVO> addGenericInteraction(String userId, String targetId,
      String targetType, InteractionType type, String metadata);

  /**
   * 取消通用互动
   */
  ResultResponse<String> removeGenericInteraction(String userId, String targetId,
      String targetType, InteractionType type);

  /**
   * 检查通用互动是否存在
   */
  ResultResponse<Boolean> checkGenericInteraction(String userId, String targetId,
      String targetType, InteractionType type);

  /**
   * 获取目标的互动数量
   */
  ResultResponse<Long> getGenericInteractionCount(String targetId, String targetType,
      InteractionType type);
}
