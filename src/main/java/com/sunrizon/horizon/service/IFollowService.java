package com.sunrizon.horizon.service;

import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.FollowVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Follow Service Interface
 */
public interface IFollowService {

  /**
   * 关注用户
   *
   * @param followerId 关注者ID
   * @param followingId 被关注者ID
   * @return ResultResponse
   */
  ResultResponse<FollowVO> followUser(String followerId, String followingId);

  /**
   * 取消关注
   *
   * @param followerId 关注者ID
   * @param followingId 被关注者ID
   * @return ResultResponse
   */
  ResultResponse<String> unfollowUser(String followerId, String followingId);

  /**
   * 检查是否已关注
   *
   * @param followerId 关注者ID
   * @param followingId 被关注者ID
   * @return ResultResponse
   */
  ResultResponse<Boolean> checkFollowing(String followerId, String followingId);

  /**
   * 获取我关注的人列表
   *
   * @param followerId 关注者ID
   * @param pageable 分页参数
   * @return ResultResponse
   */
  ResultResponse<Page<FollowVO>> getFollowingList(String followerId, Pageable pageable);

  /**
   * 获取我的粉丝列表
   *
   * @param followingId 被关注者ID
   * @param pageable 分页参数
   * @return ResultResponse
   */
  ResultResponse<Page<FollowVO>> getFollowersList(String followingId, Pageable pageable);

  /**
   * 获取关注数统计
   *
   * @param userId 用户ID
   * @return ResultResponse
   */
  ResultResponse<Long> getFollowingCount(String userId);

  /**
   * 获取粉丝数统计
   *
   * @param userId 用户ID
   * @return ResultResponse
   */
  ResultResponse<Long> getFollowersCount(String userId);
}
