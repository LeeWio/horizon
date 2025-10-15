package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Follow;
import com.sunrizon.horizon.pojo.User;
import com.sunrizon.horizon.repository.FollowRepository;
import com.sunrizon.horizon.repository.UserRepository;
import com.sunrizon.horizon.service.IFollowService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.FollowVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Follow Service Implementation
 */
@Service
@Slf4j
public class FollowServiceImpl implements IFollowService {

  @Resource
  private FollowRepository followRepository;

  @Resource
  private UserRepository userRepository;

  /**
   * 关注用户
   */
  @Override
  @Transactional
  public ResultResponse<FollowVO> followUser(String followerId, String followingId) {
    // 1. 参数校验
    if (StrUtil.isBlank(followerId) || StrUtil.isBlank(followingId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 不能关注自己
    if (StrUtil.equals(followerId, followingId)) {
      return ResultResponse.error(ResponseCode.CANNOT_FOLLOW_SELF);
    }

    // 3. 检查用户是否存在
    User follower = userRepository.findById(followerId)
        .orElse(null);
    if (follower == null) {
      return ResultResponse.error(ResponseCode.USER_NOT_FOUND);
    }

    User following = userRepository.findById(followingId)
        .orElse(null);
    if (following == null) {
      return ResultResponse.error(ResponseCode.FOLLOW_USER_NOT_FOUND);
    }

    // 4. 检查是否已关注
    if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
      return ResultResponse.error(ResponseCode.ALREADY_FOLLOWING);
    }

    // 5. 创建关注关系
    Follow follow = new Follow();
    follow.setFollowerId(followerId);
    follow.setFollowingId(followingId);
    Follow saved = followRepository.save(follow);

    // 6. 更新统计数据
    try {
      follower.setFollowingCount(follower.getFollowingCount() + 1);
      following.setFollowersCount(following.getFollowersCount() + 1);
      userRepository.saveAndFlush(follower);
      userRepository.saveAndFlush(following);
    } catch (Exception e) {
      log.error("Failed to update follow count", e);
    }

    // 7. 转换为 VO
    FollowVO vo = BeanUtil.copyProperties(saved, FollowVO.class);
    vo.setUsername(following.getUsername());
    vo.setAvatar(following.getAvatar());

    return ResultResponse.success(ResponseCode.USER_FOLLOWED, vo);
  }

  /**
   * 取消关注
   */
  @Override
  @Transactional
  public ResultResponse<String> unfollowUser(String followerId, String followingId) {
    // 1. 参数校验
    if (StrUtil.isBlank(followerId) || StrUtil.isBlank(followingId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    // 2. 检查关注关系是否存在
    if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
      return ResultResponse.error(ResponseCode.NOT_FOLLOWING);
    }

    // 3. 删除关注关系
    followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);

    // 4. 更新统计数据
    try {
      User follower = userRepository.findById(followerId).orElse(null);
      User following = userRepository.findById(followingId).orElse(null);

      if (follower != null) {
        follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));
        userRepository.saveAndFlush(follower);
      }

      if (following != null) {
        following.setFollowersCount(Math.max(0, following.getFollowersCount() - 1));
        userRepository.saveAndFlush(following);
      }
    } catch (Exception e) {
      log.error("Failed to update follow count", e);
    }

    return ResultResponse.success(ResponseCode.USER_UNFOLLOWED, "Unfollowed successfully");
  }

  /**
   * 检查是否已关注
   */
  @Override
  public ResultResponse<Boolean> checkFollowing(String followerId, String followingId) {
    if (StrUtil.isBlank(followerId) || StrUtil.isBlank(followingId)) {
      return ResultResponse.success(false);
    }

    boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    return ResultResponse.success(isFollowing);
  }

  /**
   * 获取我关注的人列表
   */
  @Override
  public ResultResponse<Page<FollowVO>> getFollowingList(String followerId, Pageable pageable) {
    if (StrUtil.isBlank(followerId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Follow> followPage = followRepository.findByFollowerId(followerId, pageable);
    
    Page<FollowVO> voPage = followPage.map(follow -> {
      FollowVO vo = BeanUtil.copyProperties(follow, FollowVO.class);
      
      // 填充被关注用户信息
      userRepository.findById(follow.getFollowingId()).ifPresent(user -> {
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
      });
      
      return vo;
    });

    return ResultResponse.success(voPage);
  }

  /**
   * 获取我的粉丝列表
   */
  @Override
  public ResultResponse<Page<FollowVO>> getFollowersList(String followingId, Pageable pageable) {
    if (StrUtil.isBlank(followingId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Page<Follow> followPage = followRepository.findByFollowingId(followingId, pageable);
    
    Page<FollowVO> voPage = followPage.map(follow -> {
      FollowVO vo = BeanUtil.copyProperties(follow, FollowVO.class);
      
      // 填充粉丝用户信息
      userRepository.findById(follow.getFollowerId()).ifPresent(user -> {
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
      });
      
      return vo;
    });

    return ResultResponse.success(voPage);
  }

  /**
   * 获取关注数统计
   */
  @Override
  public ResultResponse<Long> getFollowingCount(String userId) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    long count = followRepository.countByFollowerId(userId);
    return ResultResponse.success(count);
  }

  /**
   * 获取粉丝数统计
   */
  @Override
  public ResultResponse<Long> getFollowersCount(String userId) {
    if (StrUtil.isBlank(userId)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    long count = followRepository.countByFollowingId(userId);
    return ResultResponse.success(count);
  }
}
