package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.enums.InteractionTargetType;
import com.sunrizon.horizon.enums.InteractionType;
import com.sunrizon.horizon.enums.NotificationType;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.pojo.Comment;
import com.sunrizon.horizon.pojo.Interaction;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.repository.CommentRepository;
import com.sunrizon.horizon.repository.InteractionRepository;
import com.sunrizon.horizon.service.IInteractionService;
import com.sunrizon.horizon.service.INotificationService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.InteractionVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InteractionServiceImpl implements IInteractionService {

  @Resource
  private InteractionRepository interactionRepository;

  @Resource
  private ArticleRepository articleRepository;

  @Resource
  private CommentRepository commentRepository;

  @Resource
  private INotificationService notificationService;

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

  // ==================== 通用互动方法实现 ====================

  @Override
  @Transactional
  public ResultResponse<InteractionVO> addGenericInteraction(String userId, String targetId,
      String targetType, InteractionType type, String metadata) {
    // 1. 参数验证
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(targetId) || 
        StrUtil.isBlank(targetType) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 验证目标类型
    if (!InteractionTargetType.ARTICLE.name().equals(targetType) && 
        !InteractionTargetType.COMMENT.name().equals(targetType)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 3. 检查目标是否存在
    Comment targetComment = null;
    Article targetArticle = null;
    if (InteractionTargetType.ARTICLE.name().equals(targetType)) {
      targetArticle = articleRepository.findById(targetId).orElse(null);
      if (targetArticle == null) {
        return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND);
      }
    } else {
      targetComment = commentRepository.findById(targetId).orElse(null);
      if (targetComment == null) {
        return ResultResponse.error(ResponseCode.COMMENT_NOT_FOUND);
      }
    }

    // 4. 检查是否已存在
    if (interactionRepository.existsByTargetIdAndTargetTypeAndUserIdAndType(
        targetId, targetType, userId, type)) {
      return ResultResponse.error(ResponseCode.INTERACTION_ALREADY_EXISTS);
    }

    // 5. 创建互动记录
    Interaction interaction = new Interaction();
    interaction.setUserId(userId);
    interaction.setTargetId(targetId);
    interaction.setTargetType(targetType);
    interaction.setType(type);
    interaction.setMetadata(metadata);
    
    // 兼容旧字段
    if (InteractionTargetType.ARTICLE.name().equals(targetType)) {
      interaction.setArticleId(targetId);
    }

    // 6. 保存到数据库
    Interaction saved = interactionRepository.saveAndFlush(interaction);

    // 7. 更新统计数据
    updateTargetCount(targetId, targetType, type, 1);

    // 8. 发送通知
    try {
      if (type == InteractionType.LIKE) {
        if (InteractionTargetType.COMMENT.name().equals(targetType) && targetComment != null) {
          // 评论点赞通知：避免自己点赞自己的评论
          if (!targetComment.getUserId().equals(userId)) {
            notificationService.createNotification(
                targetComment.getUserId(),
                NotificationType.LIKE,
                "评论点赞通知",
                "您的评论收到了新的点赞",
                targetId,
                userId
            );
          }
        } else if (InteractionTargetType.ARTICLE.name().equals(targetType) && targetArticle != null) {
          // 文章点赞通知：避免自己点赞自己的文章
          if (!targetArticle.getAuthorId().equals(userId)) {
            notificationService.createNotification(
                targetArticle.getAuthorId(),
                NotificationType.LIKE,
                "文章点赞通知",
                "您的文章《" + targetArticle.getTitle() + "》收到了新的点赞",
                targetId,
                userId
            );
          }
        }
      } else if (type == InteractionType.FAVORITE && targetArticle != null) {
        // 文章收藏通知：避免自己收藏自己的文章
        if (!targetArticle.getAuthorId().equals(userId)) {
          notificationService.createNotification(
              targetArticle.getAuthorId(),
              NotificationType.FAVORITE,
              "文章收藏通知",
              "您的文章《" + targetArticle.getTitle() + "》被收藏了",
              targetId,
              userId
          );
        }
      }
    } catch (Exception e) {
      // 通知发送失败不影响互动操作
      log.warn("发送互动通知失败: {}", e.getMessage());
    }

    // 9. 转换为VO
    InteractionVO vo = BeanUtil.copyProperties(saved, InteractionVO.class);

    return ResultResponse.success(ResponseCode.SUCCESS, vo);
  }

  @Override
  @Transactional
  public ResultResponse<String> removeGenericInteraction(String userId, String targetId,
      String targetType, InteractionType type) {
    // 1. 参数验证
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(targetId) || 
        StrUtil.isBlank(targetType) || type == null) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 检查是否存在
    if (!interactionRepository.existsByTargetIdAndTargetTypeAndUserIdAndType(
        targetId, targetType, userId, type)) {
      return ResultResponse.error(ResponseCode.INTERACTION_NOT_FOUND);
    }

    // 3. 删除互动记录
    interactionRepository.deleteByTargetIdAndTargetTypeAndUserIdAndType(
        targetId, targetType, userId, type);

    // 4. 更新统计数据
    updateTargetCount(targetId, targetType, type, -1);

    return ResultResponse.success(ResponseCode.SUCCESS);
  }

  @Override
  public ResultResponse<Boolean> checkGenericInteraction(String userId, String targetId,
      String targetType, InteractionType type) {
    if (StrUtil.isBlank(userId) || StrUtil.isBlank(targetId) || 
        StrUtil.isBlank(targetType) || type == null) {
      return ResultResponse.success(false);
    }

    boolean exists = interactionRepository.existsByTargetIdAndTargetTypeAndUserIdAndType(
        targetId, targetType, userId, type);
    return ResultResponse.success(exists);
  }

  @Override
  public ResultResponse<Long> getGenericInteractionCount(String targetId, String targetType,
      InteractionType type) {
    if (StrUtil.isBlank(targetId) || StrUtil.isBlank(targetType) || type == null) {
      return ResultResponse.success(0L);
    }

    long count = interactionRepository.countByTargetIdAndTargetTypeAndType(
        targetId, targetType, type);
    return ResultResponse.success(count);
  }

  /**
   * 更新目标统计数据（文章或评论）
   */
  private void updateTargetCount(String targetId, String targetType, InteractionType type, int delta) {
    try {
      if (InteractionTargetType.ARTICLE.name().equals(targetType)) {
        Article article = articleRepository.findById(targetId).orElse(null);
        if (article != null) {
          updateArticleCount(article, type, delta);
        }
      } else if (InteractionTargetType.COMMENT.name().equals(targetType)) {
        Comment comment = commentRepository.findById(targetId).orElse(null);
        if (comment != null && type == InteractionType.LIKE) {
          comment.setLikesCount(Math.max(0, comment.getLikesCount() + delta));
          commentRepository.saveAndFlush(comment);
        }
      }
    } catch (Exception e) {
      // 忽略统计更新失败
    }
  }
}
