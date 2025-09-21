package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 文章数据访问层
 * 提供文章相关的数据库操作
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    /**
     * 根据文章标题查找文章
     */
    Optional<Article> findByTitle(String title);

    /**
     * 根据文章别名查找文章
     */
    Optional<Article> findBySlug(String slug);

    /**
     * 检查文章标题是否存在
     */
    boolean existsByTitle(String title);

    /**
     * 检查文章别名是否存在
     */
    boolean existsBySlug(String slug);

    /**
     * 根据标题查找文章（忽略大小写）
     */
    Optional<Article> findByTitleIgnoreCase(String title);

    /**
     * 根据别名查找文章（忽略大小写）
     */
    Optional<Article> findBySlugIgnoreCase(String slug);

    /**
     * 检查文章标题是否存在（忽略大小写）
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * 检查文章别名是否存在（忽略大小写）
     */
    boolean existsBySlugIgnoreCase(String slug);

    /**
     * 根据作者ID查找文章
     */
    List<Article> findByAuthorIdOrderByCreatedAtDesc(String authorId);

    /**
     * 根据作者ID查找文章（分页）
     */
    Page<Article> findByAuthorIdOrderByCreatedAtDesc(String authorId, Pageable pageable);

    /**
     * 根据状态查找文章
     */
    List<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status);

    /**
     * 根据状态查找文章（分页）
     */
    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    /**
     * 根据作者ID和状态查找文章
     */
    List<Article> findByAuthorIdAndStatusOrderByCreatedAtDesc(String authorId, ArticleStatus status);

    /**
     * 根据作者ID和状态查找文章（分页）
     */
    Page<Article> findByAuthorIdAndStatusOrderByCreatedAtDesc(String authorId, ArticleStatus status, Pageable pageable);

    /**
     * 查找已发布的文章
     */
    List<Article> findByStatusAndPublishedAtIsNotNullOrderByPublishedAtDesc(ArticleStatus status);

    /**
     * 查找已发布的文章（分页）
     */
    Page<Article> findByStatusAndPublishedAtIsNotNullOrderByPublishedAtDesc(ArticleStatus status, Pageable pageable);

    /**
     * 查找精选文章
     */
    List<Article> findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(ArticleStatus status);

    /**
     * 查找精选文章（分页）
     */
    Page<Article> findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    /**
     * 查找置顶文章
     */
    List<Article> findByIsTopTrueAndStatusOrderByCreatedAtDesc(ArticleStatus status);

    /**
     * 查找置顶文章（分页）
     */
    Page<Article> findByIsTopTrueAndStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    /**
     * 根据标题模糊查询文章
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:title% ORDER BY a.createdAt DESC")
    List<Article> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * 根据标题模糊查询文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:title% ORDER BY a.createdAt DESC")
    Page<Article> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    /**
     * 根据内容模糊查询文章
     */
    @Query("SELECT a FROM Article a WHERE a.content LIKE %:content% ORDER BY a.createdAt DESC")
    List<Article> findByContentContainingIgnoreCase(@Param("content") String content);

    /**
     * 根据内容模糊查询文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE a.content LIKE %:content% ORDER BY a.createdAt DESC")
    Page<Article> findByContentContainingIgnoreCase(@Param("content") String content, Pageable pageable);

    /**
     * 根据摘要模糊查询文章
     */
    @Query("SELECT a FROM Article a WHERE a.summary LIKE %:summary% ORDER BY a.createdAt DESC")
    List<Article> findBySummaryContainingIgnoreCase(@Param("summary") String summary);

    /**
     * 根据摘要模糊查询文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE a.summary LIKE %:summary% ORDER BY a.createdAt DESC")
    Page<Article> findBySummaryContainingIgnoreCase(@Param("summary") String summary, Pageable pageable);

    /**
     * 根据发布时间范围查找文章
     */
    @Query("SELECT a FROM Article a WHERE a.publishedAt BETWEEN :startDate AND :endDate ORDER BY a.publishedAt DESC")
    List<Article> findByPublishedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 根据发布时间范围查找文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE a.publishedAt BETWEEN :startDate AND :endDate ORDER BY a.publishedAt DESC")
    Page<Article> findByPublishedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * 根据创建时间范围查找文章
     */
    @Query("SELECT a FROM Article a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<Article> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 根据创建时间范围查找文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<Article> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * 统计指定状态的文章数量
     */
    long countByStatus(ArticleStatus status);

    /**
     * 统计指定作者的文章数量
     */
    long countByAuthorId(String authorId);

    /**
     * 统计指定作者和状态的文章数量
     */
    long countByAuthorIdAndStatus(String authorId, ArticleStatus status);

    /**
     * 统计已发布的文章数量
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.status = :status AND a.publishedAt IS NOT NULL")
    long countPublishedArticles(@Param("status") ArticleStatus status);

    /**
     * 查找最新的文章
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<Article> findLatestArticles(@Param("status") ArticleStatus status, @Param("limit") int limit);

    /**
     * 查找最新的文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status ORDER BY a.createdAt DESC")
    Page<Article> findLatestArticles(@Param("status") ArticleStatus status, Pageable pageable);

    /**
     * 查找最热门的文章（按浏览量排序）
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status ORDER BY a.stats.viewCount DESC")
    List<Article> findPopularArticles(@Param("status") ArticleStatus status, @Param("limit") int limit);

    /**
     * 查找最热门的文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status ORDER BY a.stats.viewCount DESC")
    Page<Article> findPopularArticles(@Param("status") ArticleStatus status, Pageable pageable);

    /**
     * 根据分类ID查找文章
     */
    @Query("SELECT a FROM Article a JOIN a.articleCategories ac WHERE ac.category.cid = :categoryId AND a.status = :status ORDER BY a.createdAt DESC")
    List<Article> findByCategoryId(@Param("categoryId") String categoryId, @Param("status") ArticleStatus status);

    /**
     * 根据分类ID查找文章（分页）
     */
    @Query("SELECT a FROM Article a JOIN a.articleCategories ac WHERE ac.category.cid = :categoryId AND a.status = :status ORDER BY a.createdAt DESC")
    Page<Article> findByCategoryId(@Param("categoryId") String categoryId, @Param("status") ArticleStatus status, Pageable pageable);

    /**
     * 根据标签ID查找文章
     */
    @Query("SELECT a FROM Article a JOIN a.articleTags at WHERE at.tag.tid = :tagId AND a.status = :status ORDER BY a.createdAt DESC")
    List<Article> findByTagId(@Param("tagId") String tagId, @Param("status") ArticleStatus status);

    /**
     * 根据标签ID查找文章（分页）
     */
    @Query("SELECT a FROM Article a JOIN a.articleTags at WHERE at.tag.tid = :tagId AND a.status = :status ORDER BY a.createdAt DESC")
    Page<Article> findByTagId(@Param("tagId") String tagId, @Param("status") ArticleStatus status, Pageable pageable);

    /**
     * 根据多个标签ID查找文章
     */
    @Query("SELECT DISTINCT a FROM Article a JOIN a.articleTags at WHERE at.tag.tid IN :tagIds AND a.status = :status ORDER BY a.createdAt DESC")
    List<Article> findByTagIds(@Param("tagIds") List<String> tagIds, @Param("status") ArticleStatus status);

    /**
     * 根据多个标签ID查找文章（分页）
     */
    @Query("SELECT DISTINCT a FROM Article a JOIN a.articleTags at WHERE at.tag.tid IN :tagIds AND a.status = :status ORDER BY a.createdAt DESC")
    Page<Article> findByTagIds(@Param("tagIds") List<String> tagIds, @Param("status") ArticleStatus status, Pageable pageable);

    /**
     * 根据关键词搜索文章（标题、摘要、内容）
     */
    @Query("SELECT a FROM Article a WHERE (a.title LIKE %:keyword% OR a.summary LIKE %:keyword% OR a.content LIKE %:keyword%) AND a.status = :status ORDER BY a.createdAt DESC")
    List<Article> searchByKeyword(@Param("keyword") String keyword, @Param("status") ArticleStatus status);

    /**
     * 根据关键词搜索文章（分页）
     */
    @Query("SELECT a FROM Article a WHERE (a.title LIKE %:keyword% OR a.summary LIKE %:keyword% OR a.content LIKE %:keyword%) AND a.status = :status ORDER BY a.createdAt DESC")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, @Param("status") ArticleStatus status, Pageable pageable);
}