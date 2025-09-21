package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.dto.UpdateArticleRequest;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.service.IArticleService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章管理控制器
 */
@RestController
@RequestMapping("/api/article")
@Slf4j
@Tag(name = "文章管理", description = "文章相关的API接口，包括文章的增删改查、发布管理、搜索等功能")
public class ArticleController {

    @Resource
    private IArticleService articleService;

    /**
     * 创建文章
     */
    @Operation(
        summary = "创建文章", 
        description = "创建新的文章，支持设置分类、标签、状态等属性"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "文章标题或别名已存在")
    })
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<ArticleVO> createArticle(
            @Parameter(description = "创建文章请求", required = true)
            @Valid @RequestBody CreateArticleRequest request) {
        
        log.info("创建文章请求: title={}", request.getTitle());
        return articleService.createArticle(request);
    }

    /**
     * 更新文章
     */
    @Operation(
        summary = "更新文章", 
        description = "更新指定文章的信息，包括标题、内容、分类、标签等属性"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "文章不存在"),
        @ApiResponse(responseCode = "409", description = "文章标题或别名已存在")
    })
    @PutMapping("/{aid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<ArticleVO> updateArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid,
            @Parameter(description = "更新文章请求", required = true)
            @Valid @RequestBody UpdateArticleRequest request) {
        
        log.info("更新文章请求: aid={}", aid);
        return articleService.updateArticle(aid, request);
    }

    /**
     * 删除文章
     */
    @Operation(
        summary = "删除文章", 
        description = "删除指定的文章"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @DeleteMapping("/{aid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> deleteArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid) {
        
        log.info("删除文章请求: aid={}", aid);
        return articleService.deleteArticle(aid);
    }

    /**
     * 获取文章详情
     */
    @Operation(
        summary = "获取文章详情", 
        description = "根据文章ID获取文章的详细信息"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @GetMapping("/{aid}")
    public ResultResponse<ArticleVO> getArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid) {
        
        log.info("获取文章详情请求: aid={}", aid);
        return articleService.getArticle(aid);
    }

    /**
     * 根据别名获取文章
     */
    @Operation(
        summary = "根据别名获取文章", 
        description = "根据文章别名获取文章的详细信息"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @GetMapping("/slug/{slug}")
    public ResultResponse<ArticleVO> getArticleBySlug(
            @Parameter(description = "文章别名", required = true) @PathVariable String slug) {
        
        log.info("根据别名获取文章请求: slug={}", slug);
        return articleService.getArticleBySlug(slug);
    }

    /**
     * 获取文章分页列表
     */
    @Operation(
        summary = "获取文章分页列表", 
        description = "分页获取所有文章列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<ArticleVO>> getArticles(Pageable pageable) {
        log.info("获取文章分页列表请求: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return articleService.getArticles(pageable);
    }

    /**
     * 根据状态获取文章
     */
    @Operation(
        summary = "根据状态获取文章", 
        description = "根据文章状态获取文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/status/{status}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<ArticleVO>> getArticlesByStatus(
            @Parameter(description = "文章状态", required = true) @PathVariable ArticleStatus status,
            Pageable pageable) {
        
        log.info("根据状态获取文章请求: status={}", status);
        return articleService.getArticlesByStatus(status, pageable);
    }

    /**
     * 根据作者获取文章
     */
    @Operation(
        summary = "根据作者获取文章", 
        description = "根据作者ID获取文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/author/{authorId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<ArticleVO>> getArticlesByAuthor(
            @Parameter(description = "作者ID", required = true) @PathVariable String authorId,
            Pageable pageable) {
        
        log.info("根据作者获取文章请求: authorId={}", authorId);
        return articleService.getArticlesByAuthor(authorId, pageable);
    }

    /**
     * 根据作者和状态获取文章
     */
    @Operation(
        summary = "根据作者和状态获取文章", 
        description = "根据作者ID和文章状态获取文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/author/{authorId}/status/{status}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<ArticleVO>> getArticlesByAuthorAndStatus(
            @Parameter(description = "作者ID", required = true) @PathVariable String authorId,
            @Parameter(description = "文章状态", required = true) @PathVariable ArticleStatus status,
            Pageable pageable) {
        
        log.info("根据作者和状态获取文章请求: authorId={}, status={}", authorId, status);
        return articleService.getArticlesByAuthorAndStatus(authorId, status, pageable);
    }

    /**
     * 获取已发布文章
     */
    @Operation(
        summary = "获取已发布文章", 
        description = "获取所有已发布的文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/published")
    public ResultResponse<Page<ArticleVO>> getPublishedArticles(Pageable pageable) {
        log.info("获取已发布文章请求");
        return articleService.getPublishedArticles(pageable);
    }

    /**
     * 获取精选文章
     */
    @Operation(
        summary = "获取精选文章", 
        description = "获取所有精选文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/featured")
    public ResultResponse<Page<ArticleVO>> getFeaturedArticles(Pageable pageable) {
        log.info("获取精选文章请求");
        return articleService.getFeaturedArticles(pageable);
    }

    /**
     * 获取置顶文章
     */
    @Operation(
        summary = "获取置顶文章", 
        description = "获取所有置顶文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/top")
    public ResultResponse<Page<ArticleVO>> getTopArticles(Pageable pageable) {
        log.info("获取置顶文章请求");
        return articleService.getTopArticles(pageable);
    }

    /**
     * 根据分类获取文章
     */
    @Operation(
        summary = "根据分类获取文章", 
        description = "根据分类ID获取文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/category/{categoryId}")
    public ResultResponse<Page<ArticleVO>> getArticlesByCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable String categoryId,
            Pageable pageable) {
        
        log.info("根据分类获取文章请求: categoryId={}", categoryId);
        return articleService.getArticlesByCategory(categoryId, pageable);
    }

    /**
     * 根据标签获取文章
     */
    @Operation(
        summary = "根据标签获取文章", 
        description = "根据标签ID获取文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/tag/{tagId}")
    public ResultResponse<Page<ArticleVO>> getArticlesByTag(
            @Parameter(description = "标签ID", required = true) @PathVariable String tagId,
            Pageable pageable) {
        
        log.info("根据标签获取文章请求: tagId={}", tagId);
        return articleService.getArticlesByTag(tagId, pageable);
    }

    /**
     * 根据多个标签获取文章
     */
    @Operation(
        summary = "根据多个标签获取文章", 
        description = "根据多个标签ID获取文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @PostMapping("/tags")
    public ResultResponse<Page<ArticleVO>> getArticlesByTags(
            @Parameter(description = "标签ID列表", required = true) @RequestBody List<String> tagIds,
            Pageable pageable) {
        
        log.info("根据多个标签获取文章请求: tagIds={}", tagIds);
        return articleService.getArticlesByTags(tagIds, pageable);
    }

    /**
     * 搜索文章
     */
    @Operation(
        summary = "搜索文章", 
        description = "根据关键词搜索文章，支持标题、摘要、内容搜索"
    )
    @ApiResponse(responseCode = "200", description = "搜索成功")
    @GetMapping("/search")
    public ResultResponse<Page<ArticleVO>> searchArticles(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            Pageable pageable) {
        
        log.info("搜索文章请求: keyword={}", keyword);
        return articleService.searchArticles(keyword, pageable);
    }

    /**
     * 根据时间范围获取文章
     */
    @Operation(
        summary = "根据时间范围获取文章", 
        description = "根据发布时间范围获取文章分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/date-range")
    public ResultResponse<Page<ArticleVO>> getArticlesByDateRange(
            @Parameter(description = "开始时间", required = true) @RequestParam LocalDateTime startDate,
            @Parameter(description = "结束时间", required = true) @RequestParam LocalDateTime endDate,
            Pageable pageable) {
        
        log.info("根据时间范围获取文章请求: startDate={}, endDate={}", startDate, endDate);
        return articleService.getArticlesByDateRange(startDate, endDate, pageable);
    }

    /**
     * 更新文章状态
     */
    @Operation(
        summary = "更新文章状态", 
        description = "更新文章的状态（草稿、已发布、已下架等）"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @PutMapping("/{aid}/status")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> updateArticleStatus(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid,
            @Parameter(description = "文章状态", required = true) @RequestParam ArticleStatus status) {
        
        log.info("更新文章状态请求: aid={}, status={}", aid, status);
        return articleService.updateArticleStatus(aid, status);
    }

    /**
     * 发布文章
     */
    @Operation(
        summary = "发布文章", 
        description = "将文章状态设置为已发布"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "发布成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @PutMapping("/{aid}/publish")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> publishArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid) {
        
        log.info("发布文章请求: aid={}", aid);
        return articleService.publishArticle(aid);
    }

    /**
     * 取消发布文章
     */
    @Operation(
        summary = "取消发布文章", 
        description = "将文章状态设置为草稿"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取消发布成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @PutMapping("/{aid}/unpublish")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> unpublishArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid) {
        
        log.info("取消发布文章请求: aid={}", aid);
        return articleService.unpublishArticle(aid);
    }

    /**
     * 设置文章精选状态
     */
    @Operation(
        summary = "设置文章精选状态", 
        description = "设置或取消文章的精选状态"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "设置成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @PutMapping("/{aid}/featured")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> setArticleFeatured(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid,
            @Parameter(description = "是否精选", required = true) @RequestParam Boolean isFeatured) {
        
        log.info("设置文章精选状态请求: aid={}, isFeatured={}", aid, isFeatured);
        return articleService.setArticleFeatured(aid, isFeatured);
    }

    /**
     * 设置文章置顶状态
     */
    @Operation(
        summary = "设置文章置顶状态", 
        description = "设置或取消文章的置顶状态"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "设置成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @PutMapping("/{aid}/top")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> setArticleTop(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid,
            @Parameter(description = "是否置顶", required = true) @RequestParam Boolean isTop) {
        
        log.info("设置文章置顶状态请求: aid={}, isTop={}", aid, isTop);
        return articleService.setArticleTop(aid, isTop);
    }

    /**
     * 增加文章浏览量
     */
    @Operation(
        summary = "增加文章浏览量", 
        description = "增加文章的浏览量统计"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "增加成功"),
        @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @PutMapping("/{aid}/view")
    public ResultResponse<String> incrementArticleViewCount(
            @Parameter(description = "文章ID", required = true) @PathVariable String aid) {
        
        log.info("增加文章浏览量请求: aid={}", aid);
        return articleService.incrementArticleViewCount(aid);
    }

    /**
     * 获取最新文章
     */
    @Operation(
        summary = "获取最新文章", 
        description = "获取最新的文章列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/latest")
    public ResultResponse<List<ArticleVO>> getLatestArticles(
            @Parameter(description = "限制数量", required = true) @RequestParam(defaultValue = "10") int limit) {
        
        log.info("获取最新文章请求: limit={}", limit);
        return articleService.getLatestArticles(limit);
    }

    /**
     * 获取热门文章
     */
    @Operation(
        summary = "获取热门文章", 
        description = "获取最热门的文章列表，按浏览量排序"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/popular")
    public ResultResponse<List<ArticleVO>> getPopularArticles(
            @Parameter(description = "限制数量", required = true) @RequestParam(defaultValue = "10") int limit) {
        
        log.info("获取热门文章请求: limit={}", limit);
        return articleService.getPopularArticles(limit);
    }

    /**
     * 检查文章标题是否存在
     */
    @Operation(
        summary = "检查文章标题是否存在", 
        description = "检查指定的文章标题是否已存在"
    )
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check-title")
    public ResultResponse<Boolean> checkArticleTitleExists(
            @Parameter(description = "文章标题", required = true) @RequestParam String title) {
        
        log.info("检查文章标题是否存在请求: title={}", title);
        return articleService.checkArticleTitleExists(title);
    }

    /**
     * 检查文章别名是否存在
     */
    @Operation(
        summary = "检查文章别名是否存在", 
        description = "检查指定的文章别名是否已存在"
    )
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check-slug")
    public ResultResponse<Boolean> checkArticleSlugExists(
            @Parameter(description = "文章别名", required = true) @RequestParam String slug) {
        
        log.info("检查文章别名是否存在请求: slug={}", slug);
        return articleService.checkArticleSlugExists(slug);
    }
}