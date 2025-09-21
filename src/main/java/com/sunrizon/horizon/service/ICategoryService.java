package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateCategoryRequest;
import com.sunrizon.horizon.dto.UpdateCategoryRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.CategoryVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 分类服务接口
 * 提供分类相关的业务操作
 */
public interface ICategoryService {

    /**
     * 创建分类
     *
     * @param request 创建分类请求
     * @return 创建结果
     */
    ResultResponse<CategoryVO> createCategory(CreateCategoryRequest request);

    /**
     * 更新分类
     *
     * @param cid     分类ID
     * @param request 更新分类请求
     * @return 更新结果
     */
    ResultResponse<CategoryVO> updateCategory(String cid, UpdateCategoryRequest request);

    /**
     * 删除分类
     *
     * @param cid 分类ID
     * @return 删除结果
     */
    ResultResponse<String> deleteCategory(String cid);

    /**
     * 根据ID获取分类
     *
     * @param cid 分类ID
     * @return 分类信息
     */
    ResultResponse<CategoryVO> getCategory(String cid);

    /**
     * 获取所有激活的分类
     *
     * @return 分类列表
     */
    ResultResponse<List<CategoryVO>> getActiveCategories();

    /**
     * 获取所有分类（分页）
     *
     * @param pageable 分页参数
     * @return 分类分页数据
     */
    ResultResponse<Page<CategoryVO>> getCategories(Pageable pageable);

    /**
     * 根据状态获取分类
     *
     * @param isActive 是否激活
     * @param pageable 分页参数
     * @return 分类分页数据
     */
    ResultResponse<Page<CategoryVO>> getCategoriesByStatus(Boolean isActive, Pageable pageable);

    /**
     * 根据父分类ID获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    ResultResponse<List<CategoryVO>> getCategoriesByParent(String parentId);

    /**
     * 获取顶级分类
     *
     * @return 顶级分类列表
     */
    ResultResponse<List<CategoryVO>> getTopLevelCategories();

    /**
     * 根据名称搜索分类
     *
     * @param name     分类名称
     * @param pageable 分页参数
     * @return 分类分页数据
     */
    ResultResponse<Page<CategoryVO>> searchCategoriesByName(String name, Pageable pageable);

    /**
     * 更新分类状态
     *
     * @param cid      分类ID
     * @param isActive 是否激活
     * @return 更新结果
     */
    ResultResponse<String> updateCategoryStatus(String cid, Boolean isActive);

    /**
     * 更新分类排序
     *
     * @param cid       分类ID
     * @param sortOrder 排序顺序
     * @return 更新结果
     */
    ResultResponse<String> updateCategorySortOrder(String cid, Integer sortOrder);

    /**
     * 获取分类树形结构
     *
     * @return 分类树
     */
    ResultResponse<List<CategoryVO>> getCategoryTree();

    /**
     * 检查分类名称是否存在
     *
     * @param name 分类名称
     * @return 是否存在
     */
    ResultResponse<Boolean> checkCategoryNameExists(String name);

    /**
     * 检查分类别名是否存在
     *
     * @param slug 分类别名
     * @return 是否存在
     */
    ResultResponse<Boolean> checkCategorySlugExists(String slug);
}
