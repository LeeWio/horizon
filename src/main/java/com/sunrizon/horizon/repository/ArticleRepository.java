package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.pojo.Category;
import com.sunrizon.horizon.pojo.Series;
import com.sunrizon.horizon.pojo.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    
    List<Article> findByStatus(ArticleStatus status);
    
    Page<Article> findByAuthorId(String authorId, Pageable pageable);
    
    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.summary LIKE %:keyword%")
    Page<Article> findByTitleContainingOrSummaryContaining(@Param("keyword") String keyword, Pageable pageable);
    
    // Find articles by category
    Page<Article> findByCategoriesContaining(Category category, Pageable pageable);
    
    // Find articles by series
    Page<Article> findBySeriesContaining(Series series, Pageable pageable);
    
    // Find articles by tag
    Page<Article> findByTagsContaining(Tag tag, Pageable pageable);
    
    // Find articles by status and category
    Page<Article> findByStatusAndCategoriesContaining(ArticleStatus status, Category category, Pageable pageable);
    
    // Find articles by status and series
    Page<Article> findByStatusAndSeriesContaining(ArticleStatus status, Series series, Pageable pageable);
    
    // Find articles by status and tag
    Page<Article> findByStatusAndTagsContaining(ArticleStatus status, Tag tag, Pageable pageable);
    
    // Check if an article exists by slug
    boolean existsBySlug(String slug);

    // Find trending articles by view count
    Page<Article> findByStatusOrderByViewCountDesc(ArticleStatus status, Pageable pageable);

    // Find trending articles by like count
    Page<Article> findByStatusOrderByLikeCountDesc(ArticleStatus status, Pageable pageable);

    // Find trending articles by favorite count
    Page<Article> findByStatusOrderByFavoriteCountDesc(ArticleStatus status, Pageable pageable);

    // Find trending articles by view count with time filter
    @Query("SELECT a FROM Article a WHERE a.status = :status AND a.createdAt >= :startDate ORDER BY a.viewCount DESC")
    Page<Article> findTrendingByViews(@Param("status") ArticleStatus status, @Param("startDate") LocalDateTime startDate, Pageable pageable);

    // Find trending articles by like count with time filter
    @Query("SELECT a FROM Article a WHERE a.status = :status AND a.createdAt >= :startDate ORDER BY a.likeCount DESC")
    Page<Article> findTrendingByLikes(@Param("status") ArticleStatus status, @Param("startDate") LocalDateTime startDate, Pageable pageable);

    // Find trending articles by favorite count with time filter
    @Query("SELECT a FROM Article a WHERE a.status = :status AND a.createdAt >= :startDate ORDER BY a.favoriteCount DESC")
    Page<Article> findTrendingByFavorites(@Param("status") ArticleStatus status, @Param("startDate") LocalDateTime startDate, Pageable pageable);
}
