package com.sunrizon.horizon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.pojo.ArticleStats;

/**
 * 文章统计数据访问接口
 */
@Repository
public interface ArticleStatsRepository extends JpaRepository<ArticleStats, String> {

    /**
     * 根据文章ID查找统计数据
     */
    Optional<ArticleStats> findByArticleId(String articleId);

    /**
     * 批量更新浏览次数
     */
    @Modifying
    @Query("UPDATE ArticleStats s SET s.viewCount = s.viewCount + 1 WHERE s.articleId = :articleId")
    int incrementViewCount(@Param("articleId") String articleId);

    /**
     * 批量更新点赞数
     */
    @Modifying
    @Query("UPDATE ArticleStats s SET s.likeCount = s.likeCount + 1 WHERE s.articleId = :articleId")
    int incrementLikeCount(@Param("articleId") String articleId);

    /**
     * 批量更新评论数
     */
    @Modifying
    @Query("UPDATE ArticleStats s SET s.commentCount = s.commentCount + 1 WHERE s.articleId = :articleId")
    int incrementCommentCount(@Param("articleId") String articleId);

    /**
     * 批量更新分享数
     */
    @Modifying
    @Query("UPDATE ArticleStats s SET s.shareCount = s.shareCount + 1 WHERE s.articleId = :articleId")
    int incrementShareCount(@Param("articleId") String articleId);

    /**
     * 批量更新收藏数
     */
    @Modifying
    @Query("UPDATE ArticleStats s SET s.favoriteCount = s.favoriteCount + 1 WHERE s.articleId = :articleId")
    int incrementFavoriteCount(@Param("articleId") String articleId);
}
