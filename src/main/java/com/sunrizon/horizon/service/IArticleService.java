package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务接口
 * 提供文章相关的业务操作
 */
public interface IArticleService {

    /**
     * 创建文章
     *
     * @param request 创建文章请求
     * @return 创建结果
     */
    ResultResponse<ArticleVO> createArticle(CreateArticleRequest request);

    /**
     * 更新文章
     *
     * @param aid     文章ID
     * @param request 更新文章请求
     * @return 更新结果
     */
    ResultResponse<ArticleVO> updateArticle(String aid, UpdateArticleRequest request);

    /**
     * 删除文章
     *
     * @param aid 文章ID
     * @return 删除结果
     */
    ResultResponse<String> deleteArticle(String aid);

    /**
     * 根据ID获取文章
     *
     * @param aid 文章ID
     * @return 文章信息
     */
    ResultResponse<ArticleVO> getArticle(String aid);

    /**
     * 根据别名获取文章
     *
     * @param slug 文章别名
     * @return 文章信息
     */
    ResultResponse<ArticleVO> getArticleBySlug(String slug);

    /**
     * 获取所有文章（分页）
     *
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticles(Pageable pageable);

    /**
     * 根据状态获取文章
     *
     * @param status   文章状态
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticlesByStatus(ArticleStatus status, Pageable pageable);

    /**
     * 根据作者ID获取文章
     *
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticlesByAuthor(String authorId, Pageable pageable);

    /**
     * 根据作者ID和状态获取文章
     *
     * @param authorId 作者ID
     * @param status   文章状态
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticlesByAuthorAndStatus(String authorId, ArticleStatus status, Pageable pageable);

    /**
     * 获取已发布的文章
     *
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getPublishedArticles(Pageable pageable);

    /**
     * 获取精选文章
     *
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getFeaturedArticles(Pageable pageable);

    /**
     * 获取置顶文章
     *
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getTopArticles(Pageable pageable);

    /**
     * 根据分类ID获取文章
     *
     * @param categoryId 分类ID
     * @param pageable   分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticlesByCategory(String categoryId, Pageable pageable);

    /**
     * 根据标签ID获取文章
     *
     * @param tagId    标签ID
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticlesByTag(String tagId, Pageable pageable);

    /**
     * 根据多个标签ID获取文章
     *
     * @param tagIds   标签ID列表
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticlesByTags(List<String> tagIds, Pageable pageable);

    /**
     * 搜索文章
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> searchArticles(String keyword, Pageable pageable);

    /**
     * 根据发布时间范围获取文章
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param pageable  分页参数
     * @return 文章分页数据
     */
    ResultResponse<Page<ArticleVO>> getArticlesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 更新文章状态
     *
     * @param aid    文章ID
     * @param status 文章状态
     * @return 更新结果
     */
    ResultResponse<String> updateArticleStatus(String aid, ArticleStatus status);

    /**
     * 发布文章
     *
     * @param aid 文章ID
     * @return 发布结果
     */
    ResultResponse<String> publishArticle(String aid);

    /**
     * 取消发布文章
     *
     * @param aid 文章ID
     * @return 取消发布结果
     */
    ResultResponse<String> unpublishArticle(String aid);

    /**
     * 设置文章精选状态
     *
     * @param aid        文章ID
     * @param isFeatured 是否精选
     * @return 更新结果
     */
    ResultResponse<String> setArticleFeatured(String aid, Boolean isFeatured);

    /**
     * 设置文章置顶状态
     *
     * @param aid   文章ID
     * @param isTop 是否置顶
     * @return 更新结果
     */
    ResultResponse<String> setArticleTop(String aid, Boolean isTop);

    /**
     * 增加文章浏览量
     *
     * @param aid 文章ID
     * @return 更新结果
     */
    ResultResponse<String> incrementArticleViewCount(String aid);

    /**
     * 获取最新文章
     *
     * @param limit 限制数量
     * @return 最新文章列表
     */
    ResultResponse<List<ArticleVO>> getLatestArticles(int limit);

    /**
     * 获取热门文章
     *
     * @param limit 限制数量
     * @return 热门文章列表
     */
    ResultResponse<List<ArticleVO>> getPopularArticles(int limit);

    /**
     * 检查文章标题是否存在
     *
     * @param title 文章标题
     * @return 是否存在
     */
    ResultResponse<Boolean> checkArticleTitleExists(String title);

    /**
     * 检查文章别名是否存在
     *
     * @param slug 文章别名
     * @return 是否存在
     */
    ResultResponse<Boolean> checkArticleSlugExists(String slug);
}