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
    
    // Find scheduled articles that are due to be published
    List<Article> findByStatusAndPublishTimeLessThanEqual(ArticleStatus status, LocalDateTime publishTime);

    // ==================== Search Methods ====================

    /**
     * Search articles by keyword in title or content.
     * Case-insensitive search with LIKE matching.
     *
     * @param keyword  Search keyword
     * @param status   Article status filter
     * @param pageable Pagination parameters
     * @return Page of matching articles
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, @Param("status") ArticleStatus status, Pageable pageable);

    /**
     * Advanced search: keyword + author.
     *
     * @param keyword  Search keyword
     * @param authorId Author ID
     * @param status   Article status filter
     * @param pageable Pagination parameters
     * @return Page of matching articles
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status AND a.authorId = :authorId AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchByKeywordAndAuthor(@Param("keyword") String keyword, @Param("authorId") String authorId, @Param("status") ArticleStatus status, Pageable pageable);

    /**
     * Advanced search: keyword + category.
     *
     * @param keyword  Search keyword
     * @param category Category entity
     * @param status   Article status filter
     * @param pageable Pagination parameters
     * @return Page of matching articles
     */
    @Query("SELECT a FROM Article a JOIN a.categories c WHERE a.status = :status AND c = :category AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("category") Category category, @Param("status") ArticleStatus status, Pageable pageable);

    /**
     * Advanced search: keyword + tag.
     *
     * @param keyword  Search keyword
     * @param tag      Tag entity
     * @param status   Article status filter
     * @param pageable Pagination parameters
     * @return Page of matching articles
     */
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE a.status = :status AND t = :tag AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchByKeywordAndTag(@Param("keyword") String keyword, @Param("tag") Tag tag, @Param("status") ArticleStatus status, Pageable pageable);
}
