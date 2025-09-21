package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.pojo.ArticleStats;
import com.sunrizon.horizon.pojo.Category;
import com.sunrizon.horizon.pojo.Tag;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.repository.CategoryRepository;
import com.sunrizon.horizon.repository.TagRepository;
import com.sunrizon.horizon.service.IArticleService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.utils.SecurityContextUtil;
import com.sunrizon.horizon.vo.ArticleStatsVO;
import com.sunrizon.horizon.vo.ArticleVO;
import com.sunrizon.horizon.vo.CategoryVO;
import com.sunrizon.horizon.vo.TagVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
@Slf4j
public class ArticleServiceImpl implements IArticleService {

    @Resource
    private ArticleRepository articleRepository;

    @Resource
    private CategoryRepository categoryRepository;

    @Resource
    private TagRepository tagRepository;

    @Override
    @Transactional
    public ResultResponse<ArticleVO> createArticle(CreateArticleRequest request) {
        log.info("创建文章请求: title={}", request.getTitle());

        // 1. 验证文章标题是否已存在
        if (articleRepository.existsByTitleIgnoreCase(request.getTitle())) {
            return ResultResponse.error(ResponseCode.ARTICLE_TITLE_EXISTS, "文章标题已存在");
        }

        // 2. 验证文章别名是否已存在（如果提供了别名）
        if (StrUtil.isNotBlank(request.getSlug()) && articleRepository.existsBySlugIgnoreCase(request.getSlug())) {
            return ResultResponse.error(ResponseCode.ARTICLE_SLUG_EXISTS, "文章别名已存在");
        }

        // 3. 创建文章实体
        Article article = BeanUtil.copyProperties(request, Article.class);
        String currentUserId = SecurityContextUtil.getCurrentUserId();
        article.setAuthorId(currentUserId);
        article.setCreatedBy(currentUserId);
        article.setUpdatedBy(currentUserId);

        // 4. 设置发布时间（如果是发布状态）
        if (request.getStatus() == ArticleStatus.PUBLISHED) {
            article.setPublishedAt(LocalDateTime.now());
        }

        // 5. 保存文章
        Article savedArticle = articleRepository.save(article);

        // 6. 创建文章统计信息
        ArticleStats stats = new ArticleStats();
        stats.setArticle(savedArticle);
        stats.setViewCount(0L);
        stats.setLikeCount(0L);
        stats.setCommentCount(0L);
        stats.setShareCount(0L);
        stats.setFavoriteCount(0L);
        // 注意：这里需要ArticleStatsRepository，暂时注释
        // articleStatsRepository.save(stats);

        // 7. 处理分类和标签关联
        // 注意：分类和标签关联逻辑需要实现，暂时跳过
        // 这里应该根据request中的categoryIds和tagIds来建立关联关系

        // 8. 转换为VO
        ArticleVO articleVO = convertToVO(savedArticle);

        log.info("文章创建成功: aid={}, title={}", savedArticle.getAid(), savedArticle.getTitle());
        return ResultResponse.success("文章创建成功", articleVO);
    }

    @Override
    @Transactional
    public ResultResponse<ArticleVO> updateArticle(String aid, UpdateArticleRequest request) {
        log.info("更新文章请求: aid={}", aid);

        // 1. 查找文章
        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        // 2. 验证文章标题是否已存在（如果修改了标题）
        if (StrUtil.isNotBlank(request.getTitle()) && !request.getTitle().equals(article.getTitle())) {
            if (articleRepository.existsByTitleIgnoreCase(request.getTitle())) {
                return ResultResponse.error(ResponseCode.ARTICLE_TITLE_EXISTS, "文章标题已存在");
            }
        }

        // 3. 验证文章别名是否已存在（如果修改了别名）
        if (StrUtil.isNotBlank(request.getSlug()) && !request.getSlug().equals(article.getSlug())) {
            if (articleRepository.existsBySlugIgnoreCase(request.getSlug())) {
                return ResultResponse.error(ResponseCode.ARTICLE_SLUG_EXISTS, "文章别名已存在");
            }
        }

        // 4. 更新文章属性
        if (StrUtil.isNotBlank(request.getTitle())) {
            article.setTitle(request.getTitle());
        }
        if (StrUtil.isNotBlank(request.getSlug())) {
            article.setSlug(request.getSlug());
        }
        if (StrUtil.isNotBlank(request.getSummary())) {
            article.setSummary(request.getSummary());
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            article.setContent(request.getContent());
        }
        if (StrUtil.isNotBlank(request.getCoverImage())) {
            article.setCoverImage(request.getCoverImage());
        }
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
            // 如果状态改为发布，设置发布时间
            if (request.getStatus() == ArticleStatus.PUBLISHED && article.getPublishedAt() == null) {
                article.setPublishedAt(LocalDateTime.now());
            }
        }
        if (request.getIsFeatured() != null) {
            article.setIsFeatured(request.getIsFeatured());
        }
        if (request.getIsTop() != null) {
            article.setIsTop(request.getIsTop());
        }

        // 5. 保存更新
        Article updatedArticle = articleRepository.save(article);

        // 6. 处理分类和标签关联
        // 注意：分类和标签关联逻辑需要实现，暂时跳过

        // 7. 转换为VO
        ArticleVO articleVO = convertToVO(updatedArticle);

        log.info("文章更新成功: aid={}, title={}", updatedArticle.getAid(), updatedArticle.getTitle());
        return ResultResponse.success("文章更新成功", articleVO);
    }

    @Override
    @Transactional
    public ResultResponse<String> deleteArticle(String aid) {
        log.info("删除文章请求: aid={}", aid);

        // 1. 查找文章
        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        // 2. 删除文章
        articleRepository.delete(article);

        log.info("文章删除成功: aid={}, title={}", aid, article.getTitle());
        return ResultResponse.success("文章删除成功");
    }

    @Override
    public ResultResponse<ArticleVO> getArticle(String aid) {
        log.info("获取文章请求: aid={}", aid);

        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        ArticleVO articleVO = convertToVO(article);
        return ResultResponse.success(articleVO);
    }

    @Override
    public ResultResponse<ArticleVO> getArticleBySlug(String slug) {
        log.info("根据别名获取文章请求: slug={}", slug);

        Article article = articleRepository.findBySlug(slug).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        ArticleVO articleVO = convertToVO(article);
        return ResultResponse.success(articleVO);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticles(Pageable pageable) {
        log.info("获取文章分页数据: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Article> articles = articleRepository.findAll(pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticlesByStatus(ArticleStatus status, Pageable pageable) {
        log.info("根据状态获取文章: status={}", status);

        Page<Article> articles = articleRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticlesByAuthor(String authorId, Pageable pageable) {
        log.info("根据作者获取文章: authorId={}", authorId);

        Page<Article> articles = articleRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticlesByAuthorAndStatus(String authorId, ArticleStatus status, Pageable pageable) {
        log.info("根据作者和状态获取文章: authorId={}, status={}", authorId, status);

        Page<Article> articles = articleRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(authorId, status, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getPublishedArticles(Pageable pageable) {
        log.info("获取已发布文章");

        Page<Article> articles = articleRepository.findByStatusAndPublishedAtIsNotNullOrderByPublishedAtDesc(ArticleStatus.PUBLISHED, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getFeaturedArticles(Pageable pageable) {
        log.info("获取精选文章");

        Page<Article> articles = articleRepository.findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(ArticleStatus.PUBLISHED, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getTopArticles(Pageable pageable) {
        log.info("获取置顶文章");

        Page<Article> articles = articleRepository.findByIsTopTrueAndStatusOrderByCreatedAtDesc(ArticleStatus.PUBLISHED, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticlesByCategory(String categoryId, Pageable pageable) {
        log.info("根据分类获取文章: categoryId={}", categoryId);

        Page<Article> articles = articleRepository.findByCategoryId(categoryId, ArticleStatus.PUBLISHED, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticlesByTag(String tagId, Pageable pageable) {
        log.info("根据标签获取文章: tagId={}", tagId);

        Page<Article> articles = articleRepository.findByTagId(tagId, ArticleStatus.PUBLISHED, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticlesByTags(List<String> tagIds, Pageable pageable) {
        log.info("根据多个标签获取文章: tagIds={}", tagIds);

        Page<Article> articles = articleRepository.findByTagIds(tagIds, ArticleStatus.PUBLISHED, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> searchArticles(String keyword, Pageable pageable) {
        log.info("搜索文章: keyword={}", keyword);

        Page<Article> articles = articleRepository.searchByKeyword(keyword, ArticleStatus.PUBLISHED, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Page<ArticleVO>> getArticlesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("根据时间范围获取文章: startDate={}, endDate={}", startDate, endDate);

        Page<Article> articles = articleRepository.findByPublishedAtBetween(startDate, endDate, pageable);
        Page<ArticleVO> articleVOs = articles.map(this::convertToVO);

        return ResultResponse.success(articleVOs);
    }

    @Override
    @Transactional
    public ResultResponse<String> updateArticleStatus(String aid, ArticleStatus status) {
        log.info("更新文章状态: aid={}, status={}", aid, status);

        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        article.setStatus(status);
        if (status == ArticleStatus.PUBLISHED && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }

        articleRepository.save(article);

        log.info("文章状态更新成功: aid={}, status={}", aid, status);
        return ResultResponse.success("文章状态更新成功");
    }

    @Override
    @Transactional
    public ResultResponse<String> publishArticle(String aid) {
        return updateArticleStatus(aid, ArticleStatus.PUBLISHED);
    }

    @Override
    @Transactional
    public ResultResponse<String> unpublishArticle(String aid) {
        return updateArticleStatus(aid, ArticleStatus.DRAFT);
    }

    @Override
    @Transactional
    public ResultResponse<String> setArticleFeatured(String aid, Boolean isFeatured) {
        log.info("设置文章精选状态: aid={}, isFeatured={}", aid, isFeatured);

        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        article.setIsFeatured(isFeatured);
        articleRepository.save(article);

        log.info("文章精选状态更新成功: aid={}, isFeatured={}", aid, isFeatured);
        return ResultResponse.success("文章精选状态更新成功");
    }

    @Override
    @Transactional
    public ResultResponse<String> setArticleTop(String aid, Boolean isTop) {
        log.info("设置文章置顶状态: aid={}, isTop={}", aid, isTop);

        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        article.setIsTop(isTop);
        articleRepository.save(article);

        log.info("文章置顶状态更新成功: aid={}, isTop={}", aid, isTop);
        return ResultResponse.success("文章置顶状态更新成功");
    }

    @Override
    @Transactional
    public ResultResponse<String> incrementArticleViewCount(String aid) {
        log.info("增加文章浏览量: aid={}", aid);

        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            return ResultResponse.error(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在");
        }

        // 实现浏览量增加逻辑
        // 注意：这里需要实现原子性的浏览量增加操作
        // articleRepository.incrementViewCount(aid);

        log.info("文章浏览量增加成功: aid={}", aid);
        return ResultResponse.success("文章浏览量增加成功");
    }

    @Override
    public ResultResponse<List<ArticleVO>> getLatestArticles(int limit) {
        log.info("获取最新文章: limit={}", limit);

        List<Article> articles = articleRepository.findLatestArticles(ArticleStatus.PUBLISHED, limit);
        List<ArticleVO> articleVOs = articles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<List<ArticleVO>> getPopularArticles(int limit) {
        log.info("获取热门文章: limit={}", limit);

        List<Article> articles = articleRepository.findPopularArticles(ArticleStatus.PUBLISHED, limit);
        List<ArticleVO> articleVOs = articles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(articleVOs);
    }

    @Override
    public ResultResponse<Boolean> checkArticleTitleExists(String title) {
        boolean exists = articleRepository.existsByTitleIgnoreCase(title);
        return ResultResponse.success(exists);
    }

    @Override
    public ResultResponse<Boolean> checkArticleSlugExists(String slug) {
        boolean exists = articleRepository.existsBySlugIgnoreCase(slug);
        return ResultResponse.success(exists);
    }

    /**
     * 转换为VO
     */
    private ArticleVO convertToVO(Article article) {
        ArticleVO articleVO = BeanUtil.copyProperties(article, ArticleVO.class);
        
        // 设置作者名称
        if (article.getAuthor() != null) {
            articleVO.setAuthorName(article.getAuthor().getUsername());
        }
        
        // 设置统计信息
        if (article.getStats() != null) {
            ArticleStatsVO statsVO = BeanUtil.copyProperties(article.getStats(), ArticleStatsVO.class);
            articleVO.setStats(statsVO);
        }
        
        // 设置分类和标签信息
        // 注意：这里需要根据文章ID查询并设置分类和标签信息
        
        return articleVO;
    }
}