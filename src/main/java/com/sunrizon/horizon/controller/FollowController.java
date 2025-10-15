package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.FollowRequest;
import com.sunrizon.horizon.service.IFollowService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.FollowVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * Follow Controller
 * 用户关注功能控制器
 */
@RestController
@RequestMapping("/api/follow")
@Tag(name = "User Follow", description = "User follow/unfollow APIs")
@Slf4j
public class FollowController {

  @Resource
  private IFollowService followService;

  @Resource
  private SecurityContextUtil securityContextUtil;

  // ==================== 关注操作 ====================

  @PostMapping
  @Operation(summary = "Follow a user")
  public ResultResponse<FollowVO> followUser(@Valid @RequestBody FollowRequest request) {
    String followerId = securityContextUtil.getCurrentUserId().orElse(null);
    return followService.followUser(followerId, request.getFollowingId());
  }

  @DeleteMapping("/{followingId}")
  @Operation(summary = "Unfollow a user")
  public ResultResponse<String> unfollowUser(@PathVariable String followingId) {
    String followerId = securityContextUtil.getCurrentUserId().orElse(null);
    return followService.unfollowUser(followerId, followingId);
  }

  @GetMapping("/check/{followingId}")
  @Operation(summary = "Check if following a user")
  public ResultResponse<Boolean> checkFollowing(@PathVariable String followingId) {
    String followerId = securityContextUtil.getCurrentUserId().orElse(null);
    return followService.checkFollowing(followerId, followingId);
  }

  // ==================== 关注列表 ====================

  @GetMapping("/following")
  @Operation(summary = "Get my following list")
  public ResultResponse<Page<FollowVO>> getMyFollowing(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String followerId = securityContextUtil.getCurrentUserId().orElse(null);
    return followService.getFollowingList(followerId, pageable);
  }

  @GetMapping("/followers")
  @Operation(summary = "Get my followers list")
  public ResultResponse<Page<FollowVO>> getMyFollowers(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String followingId = securityContextUtil.getCurrentUserId().orElse(null);
    return followService.getFollowersList(followingId, pageable);
  }

  @GetMapping("/following/{userId}")
  @Operation(summary = "Get user's following list")
  public ResultResponse<Page<FollowVO>> getUserFollowing(@PathVariable String userId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return followService.getFollowingList(userId, pageable);
  }

  @GetMapping("/followers/{userId}")
  @Operation(summary = "Get user's followers list")
  public ResultResponse<Page<FollowVO>> getUserFollowers(@PathVariable String userId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return followService.getFollowersList(userId, pageable);
  }

  // ==================== 统计数据 ====================

  @GetMapping("/following/count/{userId}")
  @Operation(summary = "Get following count")
  public ResultResponse<Long> getFollowingCount(@PathVariable String userId) {
    return followService.getFollowingCount(userId);
  }

  @GetMapping("/followers/count/{userId}")
  @Operation(summary = "Get followers count")
  public ResultResponse<Long> getFollowersCount(@PathVariable String userId) {
    return followService.getFollowersCount(userId);
  }
}
