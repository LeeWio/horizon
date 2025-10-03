// package com.sunrizon.horizon.service;
//
// import com.sunrizon.horizon.pojo.Article;
// import com.sunrizon.horizon.enums.ArticleStatus;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import java.util.List;
// import java.util.Optional;
//
// public interface IArticleService {
//
// /**
// * 创建新文章
// */
// Article createArticle(Article article);
//
// /**
// * 更新文章
// */
// Article updateArticle(String articleId, Article articleDetails);
//
// /**
// * 根据ID删除文章
// */
// void deleteArticle(String articleId);
//
// /**
// * 根据ID获取文章
// */
// Optional<Article> getArticleById(String articleId);
//
// /**
// * 获取所有文章（分页）
// */
// Page<Article> getAllArticles(Pageable pageable);
//
// /**
// * 根据状态获取文章列表
// */
// List<Article> getArticlesByStatus(ArticleStatus status);
//
// /**
// * 根据作者ID获取文章
// */
// Page<Article> getArticlesByAuthor(String authorId, Pageable pageable);
//
// /**
// * 发布文章（更新状态为 PUBLISHED）
// */
// Article publishArticle(String articleId);
//
// /**
// * 将文章设为草稿状态
// */
// Article draftArticle(String articleId);
//
// /**
// * 审核文章
// */
// Article reviewArticle(String articleId, ArticleStatus newStatus);
//
// /**
// * 搜索文章（按标题、摘要等）
// */
// Page<Article> searchArticles(String keyword, Pageable pageable);
//
// /**
// * 获取已发布文章列表
// */
// Page<Article> getPublishedArticles(Pageable pageable);
//
// /**
// * 获取公开的文章（根据状态，如PUBLISHED）
// */
// Page<Article> getPublicArticles(Pageable pageable);
// }
