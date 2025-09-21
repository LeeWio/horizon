package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.pojo.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 分类数据访问层
 * 提供分类相关的数据库操作
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    /**
     * 根据分类名称查找分类
     */
    Optional<Category> findByName(String name);

    /**
     * 根据分类别名查找分类
     */
    Optional<Category> findBySlug(String slug);

    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查分类别名是否存在
     */
    boolean existsBySlug(String slug);

    /**
     * 根据名称查找分类（忽略大小写）
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * 根据别名查找分类（忽略大小写）
     */
    Optional<Category> findBySlugIgnoreCase(String slug);

    /**
     * 检查分类名称是否存在（忽略大小写）
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * 检查分类别名是否存在（忽略大小写）
     */
    boolean existsBySlugIgnoreCase(String slug);

    /**
     * 查找所有激活的分类
     */
    List<Category> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * 查找所有激活的分类（分页）
     */
    Page<Category> findByIsActiveTrueOrderBySortOrderAsc(Pageable pageable);

    /**
     * 根据父分类ID查找子分类
     */
    List<Category> findByParentCidOrderBySortOrderAsc(String parentId);

    /**
     * 查找顶级分类（没有父分类的分类）
     */
    List<Category> findByParentIsNullOrderBySortOrderAsc();

    /**
     * 根据父分类ID查找子分类（分页）
     */
    Page<Category> findByParentCidOrderBySortOrderAsc(String parentId, Pageable pageable);

    /**
     * 查找顶级分类（分页）
     */
    Page<Category> findByParentIsNullOrderBySortOrderAsc(Pageable pageable);

    /**
     * 根据状态查找分类
     */
    List<Category> findByIsActiveOrderBySortOrderAsc(Boolean isActive);

    /**
     * 根据状态查找分类（分页）
     */
    Page<Category> findByIsActiveOrderBySortOrderAsc(Boolean isActive, Pageable pageable);

    /**
     * 根据名称模糊查询分类
     */
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name% ORDER BY c.sortOrder ASC")
    List<Category> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * 根据名称模糊查询分类（分页）
     */
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name% ORDER BY c.sortOrder ASC")
    Page<Category> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * 统计激活分类数量
     */
    long countByIsActiveTrue();

    /**
     * 统计指定父分类下的子分类数量
     */
    long countByParentCid(String parentId);

    /**
     * 查找文章数量大于指定值的分类
     */
    @Query("SELECT c FROM Category c WHERE c.articleCount > :minCount ORDER BY c.articleCount DESC")
    List<Category> findByArticleCountGreaterThan(@Param("minCount") Long minCount);

    /**
     * 查找文章数量大于指定值的分类（分页）
     */
    @Query("SELECT c FROM Category c WHERE c.articleCount > :minCount ORDER BY c.articleCount DESC")
    Page<Category> findByArticleCountGreaterThan(@Param("minCount") Long minCount, Pageable pageable);

    /**
     * 更新分类的文章数量
     */
    @Query("UPDATE Category c SET c.articleCount = :count WHERE c.cid = :cid")
    void updateArticleCount(@Param("cid") String cid, @Param("count") Long count);

    /**
     * 增加分类的文章数量
     */
    @Query("UPDATE Category c SET c.articleCount = c.articleCount + 1 WHERE c.cid = :cid")
    void incrementArticleCount(@Param("cid") String cid);

    /**
     * 减少分类的文章数量
     */
    @Query("UPDATE Category c SET c.articleCount = c.articleCount - 1 WHERE c.cid = :cid")
    void decrementArticleCount(@Param("cid") String cid);
}
