package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 标签数据访问层
 * 提供标签相关的数据库操作
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    /**
     * 根据标签名称查找标签
     */
    Optional<Tag> findByName(String name);

    /**
     * 根据标签别名查找标签
     */
    Optional<Tag> findBySlug(String slug);

    /**
     * 检查标签名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查标签别名是否存在
     */
    boolean existsBySlug(String slug);

    /**
     * 根据名称查找标签（忽略大小写）
     */
    Optional<Tag> findByNameIgnoreCase(String name);

    /**
     * 根据别名查找标签（忽略大小写）
     */
    Optional<Tag> findBySlugIgnoreCase(String slug);

    /**
     * 检查标签名称是否存在（忽略大小写）
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * 检查标签别名是否存在（忽略大小写）
     */
    boolean existsBySlugIgnoreCase(String slug);

    /**
     * 查找所有激活的标签
     */
    List<Tag> findByIsActiveTrueOrderByArticleCountDesc();

    /**
     * 查找所有激活的标签（分页）
     */
    Page<Tag> findByIsActiveTrueOrderByArticleCountDesc(Pageable pageable);

    /**
     * 根据状态查找标签
     */
    List<Tag> findByIsActiveOrderByArticleCountDesc(Boolean isActive);

    /**
     * 根据状态查找标签（分页）
     */
    Page<Tag> findByIsActiveOrderByArticleCountDesc(Boolean isActive, Pageable pageable);

    /**
     * 根据名称模糊查询标签
     */
    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:name% ORDER BY t.articleCount DESC")
    List<Tag> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * 根据名称模糊查询标签（分页）
     */
    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:name% ORDER BY t.articleCount DESC")
    Page<Tag> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * 统计激活标签数量
     */
    long countByIsActiveTrue();

    /**
     * 查找文章数量大于指定值的标签
     */
    @Query("SELECT t FROM Tag t WHERE t.articleCount > :minCount ORDER BY t.articleCount DESC")
    List<Tag> findByArticleCountGreaterThan(@Param("minCount") Long minCount);

    /**
     * 查找文章数量大于指定值的标签（分页）
     */
    @Query("SELECT t FROM Tag t WHERE t.articleCount > :minCount ORDER BY t.articleCount DESC")
    Page<Tag> findByArticleCountGreaterThan(@Param("minCount") Long minCount, Pageable pageable);

    /**
     * 查找最热门的标签（按文章数量排序）
     */
    @Query("SELECT t FROM Tag t WHERE t.isActive = true ORDER BY t.articleCount DESC")
    List<Tag> findTopTagsByArticleCount(@Param("limit") int limit);

    /**
     * 查找最热门的标签（分页）
     */
    @Query("SELECT t FROM Tag t WHERE t.isActive = true ORDER BY t.articleCount DESC")
    Page<Tag> findTopTagsByArticleCount(Pageable pageable);

    /**
     * 更新标签的文章数量
     */
    @Query("UPDATE Tag t SET t.articleCount = :count WHERE t.tid = :tid")
    void updateArticleCount(@Param("tid") String tid, @Param("count") Long count);

    /**
     * 增加标签的文章数量
     */
    @Query("UPDATE Tag t SET t.articleCount = t.articleCount + 1 WHERE t.tid = :tid")
    void incrementArticleCount(@Param("tid") String tid);

    /**
     * 减少标签的文章数量
     */
    @Query("UPDATE Tag t SET t.articleCount = t.articleCount - 1 WHERE t.tid = :tid")
    void decrementArticleCount(@Param("tid") String tid);

    /**
     * 根据多个标签ID查找标签
     */
    @Query("SELECT t FROM Tag t WHERE t.tid IN :tagIds")
    List<Tag> findByIds(@Param("tagIds") List<String> tagIds);

    /**
     * 查找文章数量在指定范围内的标签
     */
    @Query("SELECT t FROM Tag t WHERE t.articleCount BETWEEN :minCount AND :maxCount ORDER BY t.articleCount DESC")
    List<Tag> findByArticleCountBetween(@Param("minCount") Long minCount, @Param("maxCount") Long maxCount);

    /**
     * 查找文章数量在指定范围内的标签（分页）
     */
    @Query("SELECT t FROM Tag t WHERE t.articleCount BETWEEN :minCount AND :maxCount ORDER BY t.articleCount DESC")
    Page<Tag> findByArticleCountBetween(@Param("minCount") Long minCount, @Param("maxCount") Long maxCount, Pageable pageable);
}
