package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.enums.InteractionType;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.pojo.Interaction;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.repository.InteractionRepository;
import com.sunrizon.horizon.service.IInteractionService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.InteractionVO;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteractionServiceImpl implements IInteractionService {

  @Resource
  private InteractionRepository interactionRepository;

  @Resource
  private ArticleRepository articleRepository;

  @Override
  @Transactional
  public ResultResponse<InteractionVO> addInteraction(String userId, String articleId,
      InteractionType type, String metadata) {
    // 1. 参数验证
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(articleId) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 检查文章是否存在
    Article article = articleRepository.findById(articleId).orElse(null);
    if (article == null) {
      return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND);
    }

    // 3. 检查是否已存在
    if (interactionRepository.existsByArticleIdAndUserIdAndType(articleId, userId, type)) {
      return ResultResponse.error(ResponseCode.INTERACTION_ALREADY_EXISTS);
    }

    // 4. 创建互动记录
    Interaction interaction = new Interaction();
    interaction.setUserId(userId);
    interaction.setArticleId(articleId);
    interaction.setType(type);
    interaction.setMetadata(metadata);

    // 5. 保存到数据库
    Interaction saved = interactionRepository.saveAndFlush(interaction);

    // 6. 更新文章统计数据
    updateArticleCount(article, type, 1);

    // 7. 转换为VO
    InteractionVO vo = BeanUtil.copyProperties(saved, InteractionVO.class);

    // 8. 返回对应的成功消息
    ResponseCode responseCode = getSuccessResponseCode(type, true);
    return ResultResponse.success(responseCode, vo);
  }

  @Override
  @Transactional
  public ResultResponse<String> removeInteraction(String userId, String articleId, InteractionType type) {
    // 1. 参数验证
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(articleId) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 检查是否存在
    if (!interactionRepository.existsByArticleIdAndUserIdAndType(articleId, userId, type)) {
      return ResultResponse.error(ResponseCode.INTERACTION_NOT_FOUND);
    }

    // 3. 获取文章
    Article article = articleRepository.findById(articleId).orElse(null);

    // 4. 删除互动记录
    interactionRepository.deleteByArticleIdAndUserIdAndType(articleId, userId, type);

    // 5. 更新文章统计数据
    if (article != null) {
      updateArticleCount(article, type, -1);
    }

    // 6. 返回对应的成功消息
    ResponseCode responseCode = getSuccessResponseCode(type, false);
    return ResultResponse.success(responseCode);
  }

  @Override
  public ResultResponse<Boolean> checkInteraction(String userId, String articleId, InteractionType type) {
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(articleId) || type == null) {
      return ResultResponse.success(false);
    }

    boolean exists = interactionRepository.existsByArticleIdAndUserIdAndType(articleId, userId, type);
    return ResultResponse.success(exists);
  }

  @Override
  public ResultResponse<Long> getInteractionCount(String articleId, InteractionType type) {
    if (StrUtil.isBlank(articleId) || type == null) {
      return ResultResponse.success(0L);
    }

    long count = interactionRepository.countByArticleIdAndType(articleId, type);
    return ResultResponse.success(count);
  }

  @Override
  public ResultResponse<Page<InteractionVO>> getUserInteractions(String userId, InteractionType type,
      Pageable pageable) {
    if (StrUtil.isBlank(userId) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Interaction> page = interactionRepository.findByUserIdAndType(userId, type, pageable);
    Page<InteractionVO> voPage = page.map(i -> BeanUtil.copyProperties(i, InteractionVO.class));

    return ResultResponse.success(voPage);
  }

  @Override
  public ResultResponse<Page<InteractionVO>> getArticleInteractions(String articleId, InteractionType type,
      Pageable pageable) {
    if (StrUtil.isBlank(articleId) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Interaction> page = interactionRepository.findByArticleIdAndType(articleId, type, pageable);
    Page<InteractionVO> voPage = page.map(i -> BeanUtil.copyProperties(i, InteractionVO.class));

    return ResultResponse.success(voPage);
  }

  @Override
  public ResultResponse<Page<InteractionVO>> getUserInteractionsByMetadata(String userId, InteractionType type,
      String keyword, Pageable pageable) {
    if (StrUtil.isBlank(userId) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Interaction> page = interactionRepository.findByUserIdAndTypeAndMetadataContaining(
        userId, type, keyword, pageable);
    Page<InteractionVO> voPage = page.map(i -> BeanUtil.copyProperties(i, InteractionVO.class));

    return ResultResponse.success(voPage);
  }

  /**
   * 更新文章统计数据
   */
  private void updateArticleCount(Article article, InteractionType type, int delta) {
    try {
      switch (type) {
        case LIKE:
          article.setLikeCount(Math.max(0, article.getLikeCount() + delta));
          break;
        case FAVORITE:
          article.setFavoriteCount(Math.max(0, article.getFavoriteCount() + delta));
          break;
        case SHARE:
          article.setShareCount(Math.max(0, article.getShareCount() + delta));
          break;
      }
      articleRepository.saveAndFlush(article);
    } catch (Exception e) {
      // 忽略统计更新失败
    }
  }

  /**
   * 根据互动类型获取对应的成功响应码
   */
  private ResponseCode getSuccessResponseCode(InteractionType type, boolean isAdd) {
    if (isAdd) {
      return switch (type) {
        case LIKE -> ResponseCode.ARTICLE_LIKED;
        case FAVORITE -> ResponseCode.ARTICLE_FAVORITED;
        case SHARE -> ResponseCode.ARTICLE_SHARED;
      };
    } else {
      return switch (type) {
        case LIKE -> ResponseCode.ARTICLE_UNLIKED;
        case FAVORITE -> ResponseCode.ARTICLE_UNFAVORITED;
        default -> ResponseCode.SUCCESS;
      };
    }
  }
}
