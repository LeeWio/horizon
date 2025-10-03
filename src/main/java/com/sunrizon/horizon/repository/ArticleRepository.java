package com.sunrizon.horizon.repository;

import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    
    List<Article> findByStatus(ArticleStatus status);
    
    Page<Article> findByAuthorId(String authorId, Pageable pageable);
    
    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.summary LIKE %:keyword%")
    Page<Article> findByTitleContainingOrSummaryContaining(@Param("keyword") String keyword, Pageable pageable);
}
