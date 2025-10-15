package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.sunrizon.horizon.dto.CreateCommentRequest;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Comment;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.repository.CommentRepository;
import com.sunrizon.horizon.service.ICommentService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.XssUtil;
import com.sunrizon.horizon.vo.CommentVO;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ICommentService.
 * <p>
 * Handles comment management operations.
 */
@Service
@Slf4j
public class CommentServiceImpl implements ICommentService {

  @Resource
  private CommentRepository commentRepository;

  @Resource
  private ArticleRepository articleRepository;

  /**
   * 创建新评论
   *
   * @param request 评论创建请求
   * @return {@link ResultResponse} 包含创建的 {@link CommentVO}
   */
  @Override
  @Transactional
  public ResultResponse<CommentVO> createComment(CreateCommentRequest request) {
    // 1. 参数验证 - 检查文章是否存在
    if (!articleRepository.existsById(request.getArticleId())) {
      return ResultResponse.error(ResponseCode.COMMENT_ARTICLE_NOT_FOUND);
    }

    // 2. 如果是回复评论，检查父评论是否存在
    if (StrUtil.isNotBlank(request.getParentId())) {
      if (!commentRepository.existsById(request.getParentId())) {
        return ResultResponse.error(ResponseCode.COMMENT_PARENT_NOT_FOUND);
      }
    }

    // 3. DTO 转 Entity (使用 BeanUtil)
    Comment comment = BeanUtil.copyProperties(request, Comment.class);

    // XSS防护：清理评论内容中的恶意代码
    if (StrUtil.isNotBlank(comment.getContent())) {
      comment.setContent(XssUtil.cleanUserInput(comment.getContent()));
    }

    // 4. 保存数据
    Comment savedComment = commentRepository.save(comment);

    // 5. Entity 转 VO
    CommentVO commentVO = BeanUtil.copyProperties(savedComment, CommentVO.class);

    // 6. 返回统一响应
    return ResultResponse.success(ResponseCode.COMMENT_CREATED, commentVO);
  }

  /**
   * 根据文章ID获取所有评论
   *
   * @param articleId 文章ID
   * @return {@link ResultResponse} 包含评论列表
   */
  @Override
  public ResultResponse<List<CommentVO>> getCommentsByArticleId(String articleId) {
    // 验证参数
    if (StrUtil.isBlank(articleId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 查询评论
    List<Comment> comments = commentRepository.findByArticleId(articleId);

    // Entity 转 VO
    List<CommentVO> commentVOs = comments.stream()
        .map(comment -> BeanUtil.copyProperties(comment, CommentVO.class))
        .collect(Collectors.toList());

    // 返回响应
    return ResultResponse.success(commentVOs);
  }

  /**
   * 根据文章ID分页获取评论
   *
   * @param articleId 文章ID
   * @param pageable  分页参数
   * @return {@link ResultResponse} 包含分页评论列表
   */
  @Override
  public ResultResponse<Page<CommentVO>> getCommentsByArticleId(String articleId, Pageable pageable) {
    // 验证参数
    if (StrUtil.isBlank(articleId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 分页查询评论
    Page<Comment> commentPage = commentRepository.findByArticleId(articleId, pageable);

    // Entity 转 VO
    Page<CommentVO> voPage = commentPage.map(comment -> BeanUtil.copyProperties(comment, CommentVO.class));

    // 返回响应
    return ResultResponse.success(voPage);
  }

  /**
   * 根据评论ID获取评论
   *
   * @param cid 评论ID
   * @return {@link ResultResponse} 包含评论VO
   */
  @Override
  public ResultResponse<CommentVO> getCommentById(String cid) {
    // 验证参数
    if (StrUtil.isBlank(cid)) {
      return ResultResponse.error(ResponseCode.COMMENT_ID_CANNOT_BE_EMPTY);
    }

    // 查询评论
    Comment comment = commentRepository.findById(cid)
        .orElseThrow(() -> new RuntimeException("Comment not found with id: " + cid));

    // Entity 转 VO
    CommentVO commentVO = BeanUtil.copyProperties(comment, CommentVO.class);

    // 返回响应
    return ResultResponse.success(commentVO);
  }

  /**
   * 删除评论
   *
   * @param cid 评论ID
   * @return {@link ResultResponse} 包含成功或错误消息
   */
  @Override
  @Transactional
  public ResultResponse<String> deleteComment(String cid) {
    // 验证参数
    if (StrUtil.isBlank(cid)) {
      return ResultResponse.error(ResponseCode.COMMENT_ID_CANNOT_BE_EMPTY);
    }

    // 查询评论
    Comment comment = commentRepository.findById(cid)
        .orElseThrow(() -> new RuntimeException("Comment not found with id: " + cid));

    // 删除评论
    commentRepository.delete(comment);

    // 返回响应
    return ResultResponse.success(ResponseCode.COMMENT_DELETED_SUCCESSFULLY);
  }

  /**
   * 根据父评论ID获取子评论
   *
   * @param parentId 父评论ID
   * @return {@link ResultResponse} 包含子评论列表
   */
  @Override
  public ResultResponse<List<CommentVO>> getRepliesByParentId(String parentId) {
    // 验证参数
    if (StrUtil.isBlank(parentId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 查询子评论
    List<Comment> replies = commentRepository.findByParentId(parentId);

    // Entity 转 VO
    List<CommentVO> replyVOs = replies.stream()
        .map(comment -> BeanUtil.copyProperties(comment, CommentVO.class))
        .collect(Collectors.toList());

    // 返回响应
    return ResultResponse.success(replyVOs);
  }

  /**
   * 根据用户ID获取评论
   *
   * @param userId 用户ID
   * @return {@link ResultResponse} 包含评论列表
   */
  @Override
  public ResultResponse<List<CommentVO>> getCommentsByUserId(String userId) {
    // 验证参数
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.USER_ID_CANNOT_BE_EMPTY);
    }

    // 查询评论
    List<Comment> comments = commentRepository.findByUserId(userId);

    // Entity 转 VO
    List<CommentVO> commentVOs = comments.stream()
        .map(comment -> BeanUtil.copyProperties(comment, CommentVO.class))
        .collect(Collectors.toList());

    // 返回响应
    return ResultResponse.success(commentVOs);
  }
}
