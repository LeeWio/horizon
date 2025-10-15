package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.CreateCommentRequest;
import com.sunrizon.horizon.service.ICommentService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.CommentVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentController {

  @Resource
  private ICommentService commentService;

  /**
   * 创建新评论
   *
   * @param request 包含评论创建数据的DTO
   * @return 包含创建的评论VO的响应
   */
  @PostMapping("/create")
  public ResultResponse<CommentVO> createComment(@Valid @RequestBody CreateCommentRequest request) {
    return commentService.createComment(request);
  }

  /**
   * 根据评论ID获取评论
   *
   * @param cid 评论ID
   * @return 包含评论VO的响应
   */
  @GetMapping("/{cid}")
  public ResultResponse<CommentVO> getCommentById(@PathVariable("cid") String cid) {
    return commentService.getCommentById(cid);
  }

  /**
   * 根据文章ID获取所有评论
   *
   * @param articleId 文章ID
   * @return 包含评论列表的响应
   */
  @GetMapping("/article/{articleId}")
  public ResultResponse<List<CommentVO>> getCommentsByArticleId(@PathVariable("articleId") String articleId) {
    return commentService.getCommentsByArticleId(articleId);
  }

  /**
   * 根据文章ID分页获取评论
   *
   * @param articleId 文章ID
   * @param pageable  分页参数
   * @return 包含分页评论列表的响应
   */
  @GetMapping("/article/{articleId}/page")
  public ResultResponse<Page<CommentVO>> getCommentsByArticleIdPaged(
      @PathVariable("articleId") String articleId,
      Pageable pageable) {
    return commentService.getCommentsByArticleId(articleId, pageable);
  }

  /**
   * 根据父评论ID获取回复评论
   *
   * @param parentId 父评论ID
   * @return 包含回复评论列表的响应
   */
  @GetMapping("/replies/{parentId}")
  public ResultResponse<List<CommentVO>> getRepliesByParentId(@PathVariable("parentId") String parentId) {
    return commentService.getRepliesByParentId(parentId);
  }

  /**
   * 根据用户ID获取评论
   *
   * @param userId 用户ID
   * @return 包含评论列表的响应
   */
  @GetMapping("/user/{userId}")
  public ResultResponse<List<CommentVO>> getCommentsByUserId(@PathVariable("userId") String userId) {
    return commentService.getCommentsByUserId(userId);
  }

  /**
   * 删除评论
   *
   * @param cid 评论ID
   * @return 包含成功或错误消息的响应
   */
  @DeleteMapping("/{cid}")
  public ResultResponse<String> deleteComment(@PathVariable("cid") String cid) {
    return commentService.deleteComment(cid);
  }
}
