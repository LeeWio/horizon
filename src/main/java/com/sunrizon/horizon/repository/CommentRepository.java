package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.enums.CommentStatus;
import com.sunrizon.horizon.pojo.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  /**
   * 按状态查询评论
   */
  Page<Comment> findByStatus(CommentStatus status, Pageable pageable);

  /**
   * 按文章ID和状态查询
   */
  Page<Comment> findByArticleIdAndStatus(String articleId, CommentStatus status, Pageable pageable);

  /**
   * 按热门度排序（点赞数降序）
   */
  @Query("SELECT c FROM Comment c WHERE c.articleId = :articleId AND c.status = :status ORDER BY c.likesCount DESC, c.createdAt DESC")
  Page<Comment> findByArticleIdOrderByLikesCountDesc(
      @Param("articleId") String articleId,
      @Param("status") CommentStatus status,
      Pageable pageable);

  /**
   * 按最新排序（创建时间降序）
   */
  @Query("SELECT c FROM Comment c WHERE c.articleId = :articleId AND c.status = :status ORDER BY c.createdAt DESC")
  Page<Comment> findByArticleIdOrderByCreatedAtDesc(
      @Param("articleId") String articleId,
      @Param("status") CommentStatus status,
      Pageable pageable);

  /**
   * 按最早排序（创建时间升序）
   */
  @Query("SELECT c FROM Comment c WHERE c.articleId = :articleId AND c.status = :status ORDER BY c.createdAt ASC")
  Page<Comment> findByArticleIdOrderByCreatedAtAsc(
      @Param("articleId") String articleId,
      @Param("status") CommentStatus status,
      Pageable pageable);
}
