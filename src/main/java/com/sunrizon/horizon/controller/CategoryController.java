package com.sunrizon.horizon.controller;

import com.sunrizon.horizon.dto.CreateCategoryRequest;
import com.sunrizon.horizon.dto.UpdateCategoryRequest;
import com.sunrizon.horizon.service.ICategoryService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.CategoryVO;
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
 * 分类管理控制器
 */
@RestController
@RequestMapping("/api/category")
@Slf4j
@Tag(name = "分类管理", description = "分类相关的API接口，包括分类的增删改查、状态管理等功能")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    /**
     * 创建分类
     */
    @Operation(
        summary = "创建分类", 
        description = "创建新的分类，支持设置父分类、颜色、图标等属性"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "分类名称或别名已存在")
    })
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<CategoryVO> createCategory(
            @Parameter(description = "创建分类请求", required = true)
            @Valid @RequestBody CreateCategoryRequest request) {
        
        log.info("创建分类请求: name={}", request.getName());
        return categoryService.createCategory(request);
    }

    /**
     * 更新分类
     */
    @Operation(
        summary = "更新分类", 
        description = "更新指定分类的信息，包括名称、描述、颜色等属性"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "分类不存在"),
        @ApiResponse(responseCode = "409", description = "分类名称或别名已存在")
    })
    @PutMapping("/{cid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<CategoryVO> updateCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable String cid,
            @Parameter(description = "更新分类请求", required = true)
            @Valid @RequestBody UpdateCategoryRequest request) {
        
        log.info("更新分类请求: cid={}", cid);
        return categoryService.updateCategory(cid, request);
    }

    /**
     * 删除分类
     */
    @Operation(
        summary = "删除分类", 
        description = "删除指定的分类，如果分类下有文章或子分类则无法删除"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "分类不存在"),
        @ApiResponse(responseCode = "409", description = "分类下存在文章或子分类，无法删除")
    })
    @DeleteMapping("/{cid}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> deleteCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable String cid) {
        
        log.info("删除分类请求: cid={}", cid);
        return categoryService.deleteCategory(cid);
    }

    /**
     * 获取分类详情
     */
    @Operation(
        summary = "获取分类详情", 
        description = "根据分类ID获取分类的详细信息"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @GetMapping("/{cid}")
    public ResultResponse<CategoryVO> getCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable String cid) {
        
        log.info("获取分类详情请求: cid={}", cid);
        return categoryService.getCategory(cid);
    }

    /**
     * 获取激活的分类列表
     */
    @Operation(
        summary = "获取激活分类列表", 
        description = "获取所有激活状态的分类列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/active")
    public ResultResponse<List<CategoryVO>> getActiveCategories() {
        log.info("获取激活分类列表请求");
        return categoryService.getActiveCategories();
    }

    /**
     * 获取分类分页列表
     */
    @Operation(
        summary = "获取分类分页列表", 
        description = "分页获取所有分类列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<CategoryVO>> getCategories(Pageable pageable) {
        log.info("获取分类分页列表请求: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return categoryService.getCategories(pageable);
    }

    /**
     * 根据状态获取分类
     */
    @Operation(
        summary = "根据状态获取分类", 
        description = "根据激活状态获取分类分页列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/status/{isActive}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<CategoryVO>> getCategoriesByStatus(
            @Parameter(description = "是否激活", required = true) @PathVariable Boolean isActive,
            Pageable pageable) {
        
        log.info("根据状态获取分类请求: isActive={}", isActive);
        return categoryService.getCategoriesByStatus(isActive, pageable);
    }

    /**
     * 获取子分类
     */
    @Operation(
        summary = "获取子分类", 
        description = "根据父分类ID获取子分类列表"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/{parentId}/children")
    public ResultResponse<List<CategoryVO>> getCategoriesByParent(
            @Parameter(description = "父分类ID", required = true) @PathVariable String parentId) {
        
        log.info("获取子分类请求: parentId={}", parentId);
        return categoryService.getCategoriesByParent(parentId);
    }

    /**
     * 获取顶级分类
     */
    @Operation(
        summary = "获取顶级分类", 
        description = "获取所有顶级分类（没有父分类的分类）"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/top-level")
    public ResultResponse<List<CategoryVO>> getTopLevelCategories() {
        log.info("获取顶级分类请求");
        return categoryService.getTopLevelCategories();
    }

    /**
     * 搜索分类
     */
    @Operation(
        summary = "搜索分类", 
        description = "根据分类名称搜索分类"
    )
    @ApiResponse(responseCode = "200", description = "搜索成功")
    @GetMapping("/search")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<Page<CategoryVO>> searchCategories(
            @Parameter(description = "分类名称关键词", required = true) @RequestParam String name,
            Pageable pageable) {
        
        log.info("搜索分类请求: name={}", name);
        return categoryService.searchCategoriesByName(name, pageable);
    }

    /**
     * 更新分类状态
     */
    @Operation(
        summary = "更新分类状态", 
        description = "更新分类的激活状态"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/{cid}/status")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> updateCategoryStatus(
            @Parameter(description = "分类ID", required = true) @PathVariable String cid,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        
        log.info("更新分类状态请求: cid={}, isActive={}", cid, isActive);
        return categoryService.updateCategoryStatus(cid, isActive);
    }

    /**
     * 更新分类排序
     */
    @Operation(
        summary = "更新分类排序", 
        description = "更新分类的排序顺序"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/{cid}/sort")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResultResponse<String> updateCategorySortOrder(
            @Parameter(description = "分类ID", required = true) @PathVariable String cid,
            @Parameter(description = "排序顺序", required = true) @RequestParam Integer sortOrder) {
        
        log.info("更新分类排序请求: cid={}, sortOrder={}", cid, sortOrder);
        return categoryService.updateCategorySortOrder(cid, sortOrder);
    }

    /**
     * 获取分类树
     */
    @Operation(
        summary = "获取分类树", 
        description = "获取完整的分类树形结构"
    )
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/tree")
    public ResultResponse<List<CategoryVO>> getCategoryTree() {
        log.info("获取分类树请求");
        return categoryService.getCategoryTree();
    }

    /**
     * 检查分类名称是否存在
     */
    @Operation(
        summary = "检查分类名称是否存在", 
        description = "检查指定的分类名称是否已存在"
    )
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check-name")
    public ResultResponse<Boolean> checkCategoryNameExists(
            @Parameter(description = "分类名称", required = true) @RequestParam String name) {
        
        log.info("检查分类名称是否存在请求: name={}", name);
        return categoryService.checkCategoryNameExists(name);
    }

    /**
     * 检查分类别名是否存在
     */
    @Operation(
        summary = "检查分类别名是否存在", 
        description = "检查指定的分类别名是否已存在"
    )
    @ApiResponse(responseCode = "200", description = "检查完成")
    @GetMapping("/check-slug")
    public ResultResponse<Boolean> checkCategorySlugExists(
            @Parameter(description = "分类别名", required = true) @RequestParam String slug) {
        
        log.info("检查分类别名是否存在请求: slug={}", slug);
        return categoryService.checkCategorySlugExists(slug);
    }
}