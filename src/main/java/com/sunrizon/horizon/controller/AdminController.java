package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.AuditUserRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.service.IArticleService;
import com.sunrizon.horizon.service.ICommentService;
import com.sunrizon.horizon.service.IUserService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;
import com.sunrizon.horizon.vo.CommentVO;
import com.sunrizon.horizon.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin management controller
 * <p>
 * Provides admin-only endpoints for user management, content moderation, and system administration.
 */
@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin Management", description = "Admin-only APIs for system management")
public class AdminController {

  @Resource
  private IUserService userService;

  @Resource
  private IArticleService articleService;

  @Resource
  private ICommentService commentService;

  /**
   * Get all pending users waiting for audit
   *
   * @param pageable pagination parameters
   * @return Page of pending users
   */
  @GetMapping("/users/pending")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Get pending users", description = "Retrieve all users with PENDING status waiting for admin approval")
  public ResultResponse<Page<UserVO>> getPendingUsers(Pageable pageable) {
    log.info("Admin requesting pending users list");
    return userService.getPendingUsers(pageable);
  }

  /**
   * Audit a user (approve or reject)
   *
   * @param request audit request containing user ID, status, and optional reason
   * @return Success or error message
   */
  @PostMapping("/users/audit")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Audit user", description = "Approve (ACTIVE) or reject (BANNED) a pending user registration")
  public ResultResponse<String> auditUser(@Valid @RequestBody AuditUserRequest request) {
    log.info("Admin auditing user: {} with status: {}", request.getUserId(), request.getStatus());
    return userService.auditUser(request.getUserId(), request.getStatus(), request.getReason());
  }

  // ==================== 文章管理 ====================

  /**
   * Get articles by status for admin review
   *
   * @param status   Article status (REVIEW, DRAFT, PUBLISHED, ARCHIVED)
   * @param pageable Pagination parameters
   * @return Page of articles
   */
  @GetMapping("/articles")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Get articles by status", description = "Retrieve articles filtered by status for admin management")
  public ResultResponse<Page<ArticleVO>> getArticlesByStatus(
      @RequestParam(required = false) ArticleStatus status,
      Pageable pageable) {
    log.info("Admin requesting articles with status: {}", status);
    
    // 如果没有指定状态，返回所有文章
    if (status == null) {
      return articleService.getArticles(pageable);
    }
    
    // TODO: 需要在 ArticleService 中添加按状态查询的方法
    // 暂时返回所有文章
    return articleService.getArticles(pageable);
  }

  /**
   * Update article status (for审核)
   *
   * @param aid    Article ID
   * @param status New article status
   * @return Success or error message
   */
  @PatchMapping("/articles/{aid}/status")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update article status", description = "Admin can change article status (approve/reject/archive)")
  public ResultResponse<String> updateArticleStatus(
      @PathVariable String aid,
      @RequestParam ArticleStatus status) {
    log.info("Admin updating article {} status to: {}", aid, status);
    
    // 创建更新请求对象，只更新状态
    UpdateArticleRequest request = new UpdateArticleRequest();
    request.setStatus(status);
    
    return articleService.updateArticle(aid, request);
  }

  /**
   * Admin delete article (hard delete)
   *
   * @param aid Article ID
   * @return Success or error message
   */
  @DeleteMapping("/articles/{aid}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete article", description = "Admin permanently delete an article")
  public ResultResponse<String> deleteArticle(@PathVariable String aid) {
    log.info("Admin deleting article: {}", aid);
    return articleService.deleteArticle(aid);
  }

  // ==================== 评论管理 ====================

  /**
   * Get all comments (for moderation)
   *
   * @param articleId Optional article ID filter
   * @param userId    Optional user ID filter
   * @param pageable  Pagination parameters
   * @return Page of comments
   */
  @GetMapping("/comments")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Get all comments", description = "Retrieve comments with optional filters for admin moderation")
  public ResultResponse<Page<CommentVO>> getAllComments(
      @RequestParam(required = false) String articleId,
      @RequestParam(required = false) String userId,
      Pageable pageable) {
    log.info("Admin requesting comments - articleId: {}, userId: {}", articleId, userId);
    
    if (articleId != null) {
      return commentService.getCommentsByArticleId(articleId, pageable);
    }
    
    // TODO: 需要在 CommentService 中添加分页查询所有评论的方法
    // 暂时返回错误
    return ResultResponse.error(com.sunrizon.horizon.enums.ResponseCode.SERVICE_UNAVAILABLE);
  }

  /**
   * Delete comment (for moderation)
   *
   * @param cid Comment ID
   * @return Success or error message
   */
  @DeleteMapping("/comments/{cid}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete comment", description = "Admin delete inappropriate comments")
  public ResultResponse<String> deleteComment(@PathVariable String cid) {
    log.info("Admin deleting comment: {}", cid);
    return commentService.deleteComment(cid);
  }

  // ==================== 数据统计Dashboard ====================

  /**
   * Get admin dashboard statistics
   *
   * @return Map containing various statistics
   */
  @GetMapping("/dashboard/stats")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Get dashboard statistics", description = "Retrieve comprehensive statistics for admin dashboard")
  public ResultResponse<Map<String, Object>> getDashboardStats() {
    log.info("Admin requesting dashboard statistics");
    
    Map<String, Object> stats = new HashMap<>();
    
    // TODO: 实现详细的统计数据收集
    // 这里先返回基础结构，后续完善
    stats.put("totalUsers", 0);
    stats.put("pendingUsers", 0);
    stats.put("totalArticles", 0);
    stats.put("publishedArticles", 0);
    stats.put("reviewArticles", 0);
    stats.put("totalComments", 0);
    stats.put("totalViews", 0);
    stats.put("totalLikes", 0);
    
    return ResultResponse.success(stats);
  }
}
