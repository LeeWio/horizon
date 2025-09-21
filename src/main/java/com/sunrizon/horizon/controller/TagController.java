package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.CreateTagRequest;
import com.sunrizon.horizon.dto.UpdateTagRequest;
import com.sunrizon.horizon.service.ITagService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.TagVO;
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

import java.util.List;

/**
 * 标签管理控制器
 */
@RestController
@RequestMapping("/api/tag")
@Slf4j
@Tag(name = "标签管理", description = "标签相关的API接口，包括标签的增删改查、状态管理等功能")
public class TagController {

    @Resource
    private ITagService tagService;

    /**
     * 创建标签
     */
    @Operation(
        summary = "创建标签", 
        description = "创建新的标签，支持设置颜色、描述等属性"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "标签名称或别名已存在")
    })
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<TagVO> createTag(
            @Parameter(description = "创建标签请求", required = true)
            @Valid @RequestBody CreateTagRequest request) {
        
        log.info("创建标签请求: name={}", request.getName());
        return tagService.createTag(request);
    }

    /**
     * 更新标签
     */
    @Operation(
        summary = "更新标签", 
        description = "更新指定标签的信息，包括名称、描述、颜色等属性"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "标签不存在"),
        @ApiResponse(responseCode = "409", description = "标签名称或别名已存在")
    })
    @PutMapping("/{tid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<TagVO> updateTag(
            @Parameter(description = "标签ID", required = true) @PathVariable String tid,
            @Parameter(description = "更新标签请求", required = true)
            @Valid @RequestBody UpdateTagRequest request) {
        
        log.info("更新标签请求: tid={}", tid);
        return tagService.updateTag(tid, request);
    }

    /**
     * 删除标签
     */
    @Operation(
        summary = "删除标签", 
        description = "删除指定的标签，如果标签下有文章则无法删除"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "标签不存在"),
        @ApiResponse(responseCode = "409", description = "标签下存在文章，无法删除")
    })
    @DeleteMapping("/{tid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> deleteTag(
            @Parameter(description = "标签ID", required = true) @PathVariable String tid) {
        
        log.info("删除标签请求: tid={}", tid);
        return tagService.deleteTag(tid);
    }

    /**
     * 获取标签详情
     */
    @Operation(
        summary = "获取标签详情", 
        description = "根据标签ID获取标签的详细信息"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @GetMapping("/{tid}")
    public ResultResponse<TagVO> getTag(
            @Parameter(description = "标签ID", required = true) @PathVariable String tid) {
        
        log.info("获取标签详情请求: tid={}", tid);
        return tagService.getTag(tid);
    }

    /**
     * 获取激活的标签列表
     */
    @Operation(
        summary = "获取激活标签列表", 
        description = "获取所有激活状态的标签列表，按文章数量降序排列"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/active")
    public ResultResponse<List<TagVO>> getActiveTags() {
        log.info("获取激活标签列表请求");
        return tagService.getActiveTags();
    }

    /**
     * 获取标签分页列表
     */
    @Operation(
        summary = "获取标签分页列表", 
        description = "分页获取所有标签列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<TagVO>> getTags(Pageable pageable) {
        log.info("获取标签分页列表请求: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return tagService.getTags(pageable);
    }

    /**
     * 根据状态获取标签
     */
    @Operation(
        summary = "根据状态获取标签", 
        description = "根据激活状态获取标签分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/status/{isActive}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<TagVO>> getTagsByStatus(
            @Parameter(description = "是否激活", required = true) @PathVariable Boolean isActive,
            Pageable pageable) {
        
        log.info("根据状态获取标签请求: isActive={}", isActive);
        return tagService.getTagsByStatus(isActive, pageable);
    }

    /**
     * 搜索标签
     */
    @Operation(
        summary = "搜索标签", 
        description = "根据标签名称搜索标签"
    )
    @ApiResponse(responseCode = "200", description = "搜索成功")
    @GetMapping("/search")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<TagVO>> searchTags(
            @Parameter(description = "标签名称关键词", required = true) @RequestParam String name,
            Pageable pageable) {
        
        log.info("搜索标签请求: name={}", name);
        return tagService.searchTagsByName(name, pageable);
    }

    /**
     * 更新标签状态
     */
    @Operation(
        summary = "更新标签状态", 
        description = "更新标签的激活状态"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @PutMapping("/{tid}/status")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> updateTagStatus(
            @Parameter(description = "标签ID", required = true) @PathVariable String tid,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        
        log.info("更新标签状态请求: tid={}, isActive={}", tid, isActive);
        return tagService.updateTagStatus(tid, isActive);
    }

    /**
     * 获取热门标签
     */
    @Operation(
        summary = "获取热门标签", 
        description = "获取最热门的标签列表，按文章数量排序"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/popular")
    public ResultResponse<List<TagVO>> getPopularTags(
            @Parameter(description = "限制数量", required = true) @RequestParam(defaultValue = "10") int limit) {
        
        log.info("获取热门标签请求: limit={}", limit);
        return tagService.getPopularTags(limit);
    }

    /**
     * 获取热门标签（分页）
     */
    @Operation(
        summary = "获取热门标签分页", 
        description = "分页获取热门标签列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/popular/page")
    public ResultResponse<Page<TagVO>> getPopularTagsPage(Pageable pageable) {
        log.info("获取热门标签分页请求: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return tagService.getPopularTags(pageable);
    }

    /**
     * 根据文章数量范围获取标签
     */
    @Operation(
        summary = "根据文章数量范围获取标签", 
        description = "根据文章数量范围获取标签分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/range")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<TagVO>> getTagsByArticleCountRange(
            @Parameter(description = "最小文章数量", required = true) @RequestParam Long minCount,
            @Parameter(description = "最大文章数量", required = true) @RequestParam Long maxCount,
            Pageable pageable) {
        
        log.info("根据文章数量范围获取标签请求: minCount={}, maxCount={}", minCount, maxCount);
        return tagService.getTagsByArticleCountRange(minCount, maxCount, pageable);
    }

    /**
     * 根据多个标签ID获取标签
     */
    @Operation(
        summary = "根据ID列表获取标签", 
        description = "根据多个标签ID获取标签列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @PostMapping("/batch")
    public ResultResponse<List<TagVO>> getTagsByIds(
            @Parameter(description = "标签ID列表", required = true) @RequestBody List<String> tagIds) {
        
        log.info("根据ID列表获取标签请求: tagIds={}", tagIds);
        return tagService.getTagsByIds(tagIds);
    }

    /**
     * 检查标签名称是否存在
     */
    @Operation(
        summary = "检查标签名称是否存在", 
        description = "检查指定的标签名称是否已存在"
    )
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check-name")
    public ResultResponse<Boolean> checkTagNameExists(
            @Parameter(description = "标签名称", required = true) @RequestParam String name) {
        
        log.info("检查标签名称是否存在请求: name={}", name);
        return tagService.checkTagNameExists(name);
    }

    /**
     * 检查标签别名是否存在
     */
    @Operation(
        summary = "检查标签别名是否存在", 
        description = "检查指定的标签别名是否已存在"
    )
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check-slug")
    public ResultResponse<Boolean> checkTagSlugExists(
            @Parameter(description = "标签别名", required = true) @RequestParam String slug) {
        
        log.info("检查标签别名是否存在请求: slug={}", slug);
        return tagService.checkTagSlugExists(slug);
    }
}
