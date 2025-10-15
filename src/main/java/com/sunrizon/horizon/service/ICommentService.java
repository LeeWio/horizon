package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateCommentRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.CommentVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentService {

  /**
   * 创建评论
   *
   * @param request 评论创建请求
   * @return 包含创建的评论VO的响应
   */
  ResultResponse<CommentVO> createComment(CreateCommentRequest request);

  /**
   * 根据文章ID获取所有评论
   *
   * @param articleId 文章ID
   * @return 包含评论列表的响应
   */
  ResultResponse<List<CommentVO>> getCommentsByArticleId(String articleId);

  /**
   * 根据文章ID分页获取评论
   *
   * @param articleId 文章ID
   * @param pageable  分页参数
   * @return 包含分页评论列表的响应
   */
  ResultResponse<Page<CommentVO>> getCommentsByArticleId(String articleId, Pageable pageable);

  /**
   * 根据评论ID获取评论
   *
   * @param cid 评论ID
   * @return 包含评论VO的响应
   */
  ResultResponse<CommentVO> getCommentById(String cid);

  /**
   * 删除评论
   *
   * @param cid 评论ID
   * @return 包含成功或错误消息的响应
   */
  ResultResponse<String> deleteComment(String cid);

  /**
   * 根据父评论ID获取子评论
   *
   * @param parentId 父评论ID
   * @return 包含子评论列表的响应
   */
  ResultResponse<List<CommentVO>> getRepliesByParentId(String parentId);

  /**
   * 根据用户ID获取评论
   *
   * @param userId 用户ID
   * @return 包含评论列表的响应
   */
  ResultResponse<List<CommentVO>> getCommentsByUserId(String userId);
}
