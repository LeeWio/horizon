package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

  /**
   * 根据文章ID查询所有评论
   *
   * @param articleId 文章ID
   * @return 评论列表
   */
  List<Comment> findByArticleId(String articleId);

  /**
   * 根据文章ID分页查询评论
   *
   * @param articleId 文章ID
   * @param pageable  分页参数
   * @return 分页评论列表
   */
  Page<Comment> findByArticleId(String articleId, Pageable pageable);

  /**
   * 根据父评论ID查询子评论
   *
   * @param parentId 父评论ID
   * @return 子评论列表
   */
  List<Comment> findByParentId(String parentId);

  /**
   * 根据用户ID查询评论
   *
   * @param userId 用户ID
   * @return 评论列表
   */
  List<Comment> findByUserId(String userId);

  /**
   * 根据用户ID分页查询评论
   *
   * @param userId   用户ID
   * @param pageable 分页参数
   * @return 分页评论列表
   */
  Page<Comment> findByUserId(String userId, Pageable pageable);
}
