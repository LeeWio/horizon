package com.sunrizon.horizon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunrizon.horizon.pojo.ArticleSeo;

/**
 * 文章SEO元数据数据访问层
 */
@Repository
public interface ArticleSeoRepository extends JpaRepository<ArticleSeo, String> {

  /**
   * 根据文章ID查询SEO元数据
   */
  Optional<ArticleSeo> findByArticleId(String articleId);

  /**
   * 根据文章ID删除SEO元数据
   */
  @Modifying
  @Query("DELETE FROM ArticleSeo a WHERE a.articleId = :articleId")
  void deleteByArticleId(@Param("articleId") String articleId);

  /**
   * 查询所有包含在sitemap中的文章SEO数据
   */
  @Query("SELECT a FROM ArticleSeo a WHERE a.includeInSitemap = true ORDER BY a.sitemapPriority DESC, a.updatedAt DESC")
  List<ArticleSeo> findAllIncludedInSitemap();

  /**
   * 根据文章ID列表查询SEO元数据
   */
  @Query("SELECT a FROM ArticleSeo a WHERE a.articleId IN :articleIds")
  List<ArticleSeo> findByArticleIdIn(@Param("articleIds") List<String> articleIds);

  /**
   * 检查文章是否已有SEO配置
   */
  boolean existsByArticleId(String articleId);

  /**
   * 批量更新sitemap包含状态
   */
  @Modifying
  @Query("UPDATE ArticleSeo a SET a.includeInSitemap = :include WHERE a.articleId IN :articleIds")
  void updateIncludeInSitemap(@Param("articleIds") List<String> articleIds, @Param("include") Boolean include);
}
