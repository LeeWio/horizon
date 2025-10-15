package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.InteractionRequest;
import com.sunrizon.horizon.enums.InteractionType;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.service.IInteractionService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.InteractionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * Interaction Controller
 */
@RestController
@RequestMapping("/api/interaction")
@Tag(name = "Article Interaction", description = "Article interaction APIs (like/favorite/share)")
public class InteractionController {

  @Resource
  private IInteractionService interactionService;

  @Resource
  private SecurityContextUtil securityContextUtil;

  // ==================== 点赞相关 ====================

  @PostMapping("/like")
  @Operation(summary = "Like article")
  public ResultResponse<InteractionVO> likeArticle(@RequestBody InteractionRequest request) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.addInteraction(userId, request.getArticleId(), InteractionType.LIKE, null);
  }

  @DeleteMapping("/like/{articleId}")
  @Operation(summary = "Unlike article")
  public ResultResponse<String> unlikeArticle(@PathVariable String articleId) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.removeInteraction(userId, articleId, InteractionType.LIKE);
  }

  @GetMapping("/like/check/{articleId}")
  @Operation(summary = "Check if liked")
  public ResultResponse<Boolean> checkLike(@PathVariable String articleId) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.checkInteraction(userId, articleId, InteractionType.LIKE);
  }

  @GetMapping("/like/count/{articleId}")
  @Operation(summary = "Get like count")
  public ResultResponse<Long> getLikeCount(@PathVariable String articleId) {
    return interactionService.getInteractionCount(articleId, InteractionType.LIKE);
  }

  @GetMapping("/like/my-likes")
  @Operation(summary = "Get my liked articles")
  public ResultResponse<Page<InteractionVO>> getMyLikes(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.getUserInteractions(userId, InteractionType.LIKE, pageable);
  }

  @GetMapping("/like/likers/{articleId}")
  @Operation(summary = "Get article likers")
  public ResultResponse<Page<InteractionVO>> getLikers(@PathVariable String articleId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return interactionService.getArticleInteractions(articleId, InteractionType.LIKE, pageable);
  }

  // ==================== 收藏相关 ====================

  @PostMapping("/favorite")
  @Operation(summary = "Favorite article")
  public ResultResponse<InteractionVO> favoriteArticle(@RequestBody InteractionRequest request) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.addInteraction(userId, request.getArticleId(), InteractionType.FAVORITE,
        request.getMetadata());
  }

  @DeleteMapping("/favorite/{articleId}")
  @Operation(summary = "Unfavorite article")
  public ResultResponse<String> unfavoriteArticle(@PathVariable String articleId) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.removeInteraction(userId, articleId, InteractionType.FAVORITE);
  }

  @GetMapping("/favorite/check/{articleId}")
  @Operation(summary = "Check if favorited")
  public ResultResponse<Boolean> checkFavorite(@PathVariable String articleId) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.checkInteraction(userId, articleId, InteractionType.FAVORITE);
  }

  @GetMapping("/favorite/count/{articleId}")
  @Operation(summary = "Get favorite count")
  public ResultResponse<Long> getFavoriteCount(@PathVariable String articleId) {
    return interactionService.getInteractionCount(articleId, InteractionType.FAVORITE);
  }

  @GetMapping("/favorite/my-favorites")
  @Operation(summary = "Get my favorited articles")
  public ResultResponse<Page<InteractionVO>> getMyFavorites(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.getUserInteractions(userId, InteractionType.FAVORITE, pageable);
  }

  @GetMapping("/favorite/folder/{folderName}")
  @Operation(summary = "Get favorites by folder")
  public ResultResponse<Page<InteractionVO>> getFavoritesByFolder(@PathVariable String folderName,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.getUserInteractionsByMetadata(userId, InteractionType.FAVORITE, folderName, pageable);
  }

  // ==================== 分享相关 ====================

  @PostMapping("/share")
  @Operation(summary = "Share article")
  public ResultResponse<InteractionVO> shareArticle(@RequestBody InteractionRequest request) {
    String userId = securityContextUtil.getCurrentUserId().orElse(null);
    return interactionService.addInteraction(userId, request.getArticleId(), InteractionType.SHARE,
        request.getMetadata());
  }

  @GetMapping("/share/count/{articleId}")
  @Operation(summary = "Get share count")
  public ResultResponse<Long> getShareCount(@PathVariable String articleId) {
    return interactionService.getInteractionCount(articleId, InteractionType.SHARE);
  }
}
