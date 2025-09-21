package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sunrizon.horizon.dto.CreateCategoryRequest;
import com.sunrizon.horizon.dto.UpdateCategoryRequest;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Category;
import com.sunrizon.horizon.repository.CategoryRepository;
import com.sunrizon.horizon.service.ICategoryService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Service
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Resource
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ResultResponse<CategoryVO> createCategory(CreateCategoryRequest request) {
        log.info("创建分类请求: name={}", request.getName());

        // 1. 验证分类名称是否已存在
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS, "分类名称已存在");
        }

        // 2. 验证分类别名是否已存在（如果提供了别名）
        if (StrUtil.isNotBlank(request.getSlug()) && categoryRepository.existsBySlugIgnoreCase(request.getSlug())) {
            return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS, "分类别名已存在");
        }

        // 3. 验证父分类是否存在（如果提供了父分类ID）
        Category parent = null;
        if (StrUtil.isNotBlank(request.getParentId())) {
            parent = categoryRepository.findById(request.getParentId()).orElse(null);
            if (parent == null) {
                return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND, "父分类不存在");
            }
        }

        // 4. 创建分类实体
        Category category = BeanUtil.copyProperties(request, Category.class);
        category.setParent(parent);
        category.setArticleCount(0L);

        // 5. 保存分类
        Category savedCategory = categoryRepository.save(category);

        // 6. 转换为VO
        CategoryVO categoryVO = convertToVO(savedCategory);

        log.info("分类创建成功: cid={}, name={}", savedCategory.getCid(), savedCategory.getName());
        return ResultResponse.success("分类创建成功", categoryVO);
    }

    @Override
    @Transactional
    public ResultResponse<CategoryVO> updateCategory(String cid, UpdateCategoryRequest request) {
        log.info("更新分类请求: cid={}", cid);

        // 1. 查找分类
        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) {
            return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND, "分类不存在");
        }

        // 2. 验证分类名称是否已存在（如果修改了名称）
        if (StrUtil.isNotBlank(request.getName()) && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
                return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS, "分类名称已存在");
            }
        }

        // 3. 验证分类别名是否已存在（如果修改了别名）
        if (StrUtil.isNotBlank(request.getSlug()) && !request.getSlug().equals(category.getSlug())) {
            if (categoryRepository.existsBySlugIgnoreCase(request.getSlug())) {
                return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS, "分类别名已存在");
            }
        }

        // 4. 验证父分类是否存在（如果修改了父分类）
        if (StrUtil.isNotBlank(request.getParentId()) && !request.getParentId().equals(category.getParent() != null ? category.getParent().getCid() : null)) {
            Category parent = categoryRepository.findById(request.getParentId()).orElse(null);
            if (parent == null) {
                return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND, "父分类不存在");
            }
            category.setParent(parent);
        }

        // 5. 更新分类属性
        if (StrUtil.isNotBlank(request.getName())) {
            category.setName(request.getName());
        }
        if (StrUtil.isNotBlank(request.getSlug())) {
            category.setSlug(request.getSlug());
        }
        if (StrUtil.isNotBlank(request.getDescription())) {
            category.setDescription(request.getDescription());
        }
        if (StrUtil.isNotBlank(request.getColor())) {
            category.setColor(request.getColor());
        }
        if (StrUtil.isNotBlank(request.getIcon())) {
            category.setIcon(request.getIcon());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        // 6. 保存更新
        Category updatedCategory = categoryRepository.save(category);

        // 7. 转换为VO
        CategoryVO categoryVO = convertToVO(updatedCategory);

        log.info("分类更新成功: cid={}, name={}", updatedCategory.getCid(), updatedCategory.getName());
        return ResultResponse.success("分类更新成功", categoryVO);
    }

    @Override
    @Transactional
    public ResultResponse<String> deleteCategory(String cid) {
        log.info("删除分类请求: cid={}", cid);

        // 1. 查找分类
        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) {
            return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND, "分类不存在");
        }

        // 2. 检查是否有子分类
        if (categoryRepository.countByParentCid(cid) > 0) {
            return ResultResponse.error(ResponseCode.CATEGORY_HAS_ARTICLES, "分类下存在子分类，无法删除");
        }

        // 3. 检查是否有文章
        if (category.getArticleCount() > 0) {
            return ResultResponse.error(ResponseCode.CATEGORY_HAS_ARTICLES, "分类下存在文章，无法删除");
        }

        // 4. 删除分类
        categoryRepository.delete(category);

        log.info("分类删除成功: cid={}, name={}", cid, category.getName());
        return ResultResponse.success("分类删除成功");
    }

    @Override
    public ResultResponse<CategoryVO> getCategory(String cid) {
        log.info("获取分类请求: cid={}", cid);

        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) {
            return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND, "分类不存在");
        }

        CategoryVO categoryVO = convertToVO(category);
        return ResultResponse.success(categoryVO);
    }

    @Override
    public ResultResponse<List<CategoryVO>> getActiveCategories() {
        log.info("获取激活分类列表");

        List<Category> categories = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
        List<CategoryVO> categoryVOs = categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(categoryVOs);
    }

    @Override
    public ResultResponse<Page<CategoryVO>> getCategories(Pageable pageable) {
        log.info("获取分类分页数据: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryVO> categoryVOs = categories.map(this::convertToVO);

        return ResultResponse.success(categoryVOs);
    }

    @Override
    public ResultResponse<Page<CategoryVO>> getCategoriesByStatus(Boolean isActive, Pageable pageable) {
        log.info("根据状态获取分类: isActive={}", isActive);

        Page<Category> categories = categoryRepository.findByIsActiveOrderBySortOrderAsc(isActive, pageable);
        Page<CategoryVO> categoryVOs = categories.map(this::convertToVO);

        return ResultResponse.success(categoryVOs);
    }

    @Override
    public ResultResponse<List<CategoryVO>> getCategoriesByParent(String parentId) {
        log.info("获取子分类: parentId={}", parentId);

        List<Category> categories = categoryRepository.findByParentCidOrderBySortOrderAsc(parentId);
        List<CategoryVO> categoryVOs = categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(categoryVOs);
    }

    @Override
    public ResultResponse<List<CategoryVO>> getTopLevelCategories() {
        log.info("获取顶级分类");

        List<Category> categories = categoryRepository.findByParentIsNullOrderBySortOrderAsc();
        List<CategoryVO> categoryVOs = categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(categoryVOs);
    }

    @Override
    public ResultResponse<Page<CategoryVO>> searchCategoriesByName(String name, Pageable pageable) {
        log.info("搜索分类: name={}", name);

        Page<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        Page<CategoryVO> categoryVOs = categories.map(this::convertToVO);

        return ResultResponse.success(categoryVOs);
    }

    @Override
    @Transactional
    public ResultResponse<String> updateCategoryStatus(String cid, Boolean isActive) {
        log.info("更新分类状态: cid={}, isActive={}", cid, isActive);

        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) {
            return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND, "分类不存在");
        }

        category.setIsActive(isActive);
        categoryRepository.save(category);

        log.info("分类状态更新成功: cid={}, isActive={}", cid, isActive);
        return ResultResponse.success("分类状态更新成功");
    }

    @Override
    @Transactional
    public ResultResponse<String> updateCategorySortOrder(String cid, Integer sortOrder) {
        log.info("更新分类排序: cid={}, sortOrder={}", cid, sortOrder);

        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) {
            return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND, "分类不存在");
        }

        category.setSortOrder(sortOrder);
        categoryRepository.save(category);

        log.info("分类排序更新成功: cid={}, sortOrder={}", cid, sortOrder);
        return ResultResponse.success("分类排序更新成功");
    }

    @Override
    public ResultResponse<List<CategoryVO>> getCategoryTree() {
        log.info("获取分类树");

        List<Category> topLevelCategories = categoryRepository.findByParentIsNullOrderBySortOrderAsc();
        List<CategoryVO> categoryTree = topLevelCategories.stream()
                .map(this::buildCategoryTree)
                .collect(Collectors.toList());

        return ResultResponse.success(categoryTree);
    }

    @Override
    public ResultResponse<Boolean> checkCategoryNameExists(String name) {
        boolean exists = categoryRepository.existsByNameIgnoreCase(name);
        return ResultResponse.success(exists);
    }

    @Override
    public ResultResponse<Boolean> checkCategorySlugExists(String slug) {
        boolean exists = categoryRepository.existsBySlugIgnoreCase(slug);
        return ResultResponse.success(exists);
    }

    /**
     * 构建分类树
     */
    private CategoryVO buildCategoryTree(Category category) {
        CategoryVO categoryVO = convertToVO(category);
        
        List<Category> children = categoryRepository.findByParentCidOrderBySortOrderAsc(category.getCid());
        List<CategoryVO> childrenVOs = children.stream()
                .map(this::buildCategoryTree)
                .collect(Collectors.toList());
        
        categoryVO.setChildren(childrenVOs);
        return categoryVO;
    }

    /**
     * 转换为VO
     */
    private CategoryVO convertToVO(Category category) {
        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        
        if (category.getParent() != null) {
            categoryVO.setParentId(category.getParent().getCid());
            categoryVO.setParentName(category.getParent().getName());
        }
        
        return categoryVO;
    }
}
