package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Follow Repository
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

  /**
   * 检查是否已关注
   */
  boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);

  /**
   * 查找关注关系
   */
  Optional<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);

  /**
   * 获取用户的关注列表（我关注的人）
   */
  Page<Follow> findByFollowerId(String followerId, Pageable pageable);

  /**
   * 获取用户的粉丝列表（关注我的人）
   */
  Page<Follow> findByFollowingId(String followingId, Pageable pageable);

  /**
   * 统计关注数
   */
  long countByFollowerId(String followerId);

  /**
   * 统计粉丝数
   */
  long countByFollowingId(String followingId);

  /**
   * 删除关注关系
   */
  void deleteByFollowerIdAndFollowingId(String followerId, String followingId);
}
