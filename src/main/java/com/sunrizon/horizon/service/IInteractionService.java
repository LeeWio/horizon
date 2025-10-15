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
}
