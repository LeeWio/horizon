package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.CreateCommentRequest;
import com.sunrizon.horizon.enums.CommentStatus;
import com.sunrizon.horizon.enums.InteractionTargetType;
import com.sunrizon.horizon.enums.InteractionType;
import com.sunrizon.horizon.service.ICommentService;
import com.sunrizon.horizon.service.IInteractionService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.CommentVO;
import com.sunrizon.horizon.vo.InteractionVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@Slf4j
@Tag(name = "Comment Management", description = "评论管理API")
public class CommentController {

  @Resource
  private ICommentService commentService;

  @Resource
  private IInteractionService interactionService;

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
  @Operation(summary = "删除评论", description = "删除指定的评论")
  public ResultResponse<String> deleteComment(@PathVariable("cid") String cid) {
    return commentService.deleteComment(cid);
  }

  // ==================== 新增功能 ====================

  /**
   * 按排序方式获取评论
   *
   * @param articleId 文章ID
   * @param sortBy    排序方式（HOT/LATEST/EARLIEST）
   * @param pageable  分页参数
   * @return 分页评论列表
   */
  @GetMapping("/article/{articleId}/sorted")
  @Operation(summary = "按排序获取评论", description = "支持HOT(热门)/LATEST(最新)/EARLIEST(最早)排序")
  public ResultResponse<Page<CommentVO>> getCommentsSorted(
      @PathVariable("articleId") String articleId,
      @RequestParam(defaultValue = "LATEST") String sortBy,
      Pageable pageable) {
    log.info("获取文章 {} 的评论，排序方式: {}", articleId, sortBy);
    return commentService.getCommentsBySortType(articleId, sortBy, pageable);
  }

  /**
   * 评论点赞
   */
  @PostMapping("/{cid}/like")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "评论点赞", description = "对指定评论点赞")
  public ResultResponse<InteractionVO> likeComment(@PathVariable("cid") String cid) {
    String userId = new SecurityContextUtil().getCurrentUserId()
        .orElseThrow(() -> new RuntimeException("User not authenticated"));
    log.info("用户 {} 点赞评论: {}", userId, cid);
    return interactionService.addGenericInteraction(
        userId, cid, InteractionTargetType.COMMENT.name(), InteractionType.LIKE, null);
  }

  /**
   * 取消评论点赞
   */
  @DeleteMapping("/{cid}/like")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "取消评论点赞", description = "取消对指定评论的点赞")
  public ResultResponse<String> unlikeComment(@PathVariable("cid") String cid) {
    String userId = new SecurityContextUtil().getCurrentUserId()
        .orElseThrow(() -> new RuntimeException("User not authenticated"));
    log.info("用户 {} 取消点赞评论: {}", userId, cid);
    return interactionService.removeGenericInteraction(
        userId, cid, InteractionTargetType.COMMENT.name(), InteractionType.LIKE);
  }

  /**
   * 检查是否已点赞评论
   */
  @GetMapping("/{cid}/like/check")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "检查评论点赞状态", description = "检查当前用户是否已点赞该评论")
  public ResultResponse<Boolean> checkCommentLike(@PathVariable("cid") String cid) {
    String userId = new SecurityContextUtil().getCurrentUserId()
        .orElseThrow(() -> new RuntimeException("User not authenticated"));
    return interactionService.checkGenericInteraction(
        userId, cid, InteractionTargetType.COMMENT.name(), InteractionType.LIKE);
  }

  /**
   * 获取评论点赞数
   */
  @GetMapping("/{cid}/like/count")
  @Operation(summary = "获取评论点赞数", description = "获取指定评论的点赞数量")
  public ResultResponse<Long> getCommentLikeCount(@PathVariable("cid") String cid) {
    return interactionService.getGenericInteractionCount(
        cid, InteractionTargetType.COMMENT.name(), InteractionType.LIKE);
  }

  // ==================== 管理员功能 ====================

  /**
   * 审核评论（管理员）
   */
  @PatchMapping("/{cid}/audit")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "审核评论", description = "管理员审核评论状态")
  public ResultResponse<String> auditComment(
      @PathVariable("cid") String cid,
      @RequestParam CommentStatus status) {
    log.info("管理员审核评论 {} 为 {}", cid, status);
    return commentService.auditComment(cid, status);
  }

  /**
   * 按状态查询评论（管理员）
   */
  @GetMapping("/status/{status}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "按状态查询评论", description = "管理员按状态筛选评论")
  public ResultResponse<Page<CommentVO>> getCommentsByStatus(
      @PathVariable("status") CommentStatus status,
      Pageable pageable) {
    log.info("管理员查询状态为 {} 的评论", status);
    return commentService.getCommentsByStatus(status, pageable);
  }
}
