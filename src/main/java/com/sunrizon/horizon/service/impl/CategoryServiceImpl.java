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

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ICategoryService.
 * <p>
 * Handles category management operations including creation, update, deletion,
 * and retrieval.
 */
@Service
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Resource
    private CategoryRepository categoryRepository;

    /**
     * Create a new category.
     *
     * Validates uniqueness, and saves category.
     *
     * @param request Category creation request
     * @return {@link ResultResponse} with created {@link CategoryVO}
     */
    @Override
    @Transactional
    public ResultResponse<CategoryVO> createCategory(CreateCategoryRequest request) {
        // Check name uniqueness
        if (categoryRepository.existsByName(request.getName())) {
            return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS);
        }

        // Check slug uniqueness if provided
        if (StrUtil.isNotBlank(request.getSlug()) && categoryRepository.existsBySlug(request.getSlug())) {
            return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS);
        }

        // Map DTO to Entity
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());

        // If slug is not provided, generate it from name
        if (StrUtil.isBlank(category.getSlug())) {
            category.setSlug(generateSlugFromName(request.getName()));
        }

        // Set parent category if provided
        if (StrUtil.isNotBlank(request.getParentId())) {
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with ID: " + request.getParentId()));
            category.setParent(parentCategory);
        }

        // Save category
        Category savedCategory = categoryRepository.save(category);

        // Convert to VO and return
        CategoryVO categoryVO = BeanUtil.copyProperties(savedCategory, CategoryVO.class);
        // Set parent ID for VO
        if (savedCategory.getParent() != null) {
            categoryVO.setParentId(savedCategory.getParent().getCid());
        }
        return ResultResponse.success(ResponseCode.CATEGORY_CREATED, categoryVO);
    }

    /**
     * Get a category by ID.
     *
     * @param cid Category ID
     * @return {@link ResultResponse} with {@link CategoryVO} or error
     */
    @Override
    public ResultResponse<CategoryVO> getCategory(String cid) {
        // Validate input
        if (StrUtil.isBlank(cid)) {
            return ResultResponse.error(ResponseCode.CATEGORY_ID_CANNOT_BE_EMPTY);
        }

        // Load category by ID
        Category category = categoryRepository.findById(cid)
                .orElseThrow(() -> new RuntimeException("Category not found with cid: " + cid));

        // Map entity to VO
        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        // Set parent ID for VO
        if (category.getParent() != null) {
            categoryVO.setParentId(category.getParent().getCid());
        }

        // Return response
        return ResultResponse.success(categoryVO);
    }

    /**
     * Get a paginated list of categories.
     *
     * @param pageable Pagination and sorting info
     * @return {@link ResultResponse} with paginated {@link CategoryVO} list
     */
    @Override
    public ResultResponse<Page<CategoryVO>> getCategories(Pageable pageable) {
        // Fetch paginated categories
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        // Map entity to VO
        Page<CategoryVO> voPage = categoryPage.map(category -> {
            CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
            // Set parent ID for VO
            if (category.getParent() != null) {
                categoryVO.setParentId(category.getParent().getCid());
            }
            return categoryVO;
        });

        // Return response
        return ResultResponse.success(voPage);
    }

    /**
     * Delete a category by ID.
     *
     * @param cid Category ID
     * @return {@link ResultResponse} with success or error message
     */
    @Override
    @Transactional
    public ResultResponse<String> deleteCategory(String cid) {
        // Validate input
        if (StrUtil.isBlank(cid)) {
            return ResultResponse.error(ResponseCode.CATEGORY_ID_CANNOT_BE_EMPTY);
        }

        // Find category
        Category category = categoryRepository.findById(cid)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + cid));

        // Check if category has associated articles (add this logic if needed)
        // This would require the relationship to be loaded to check if there are any articles

        // Delete category
        categoryRepository.delete(category);

        return ResultResponse.success(ResponseCode.CATEGORY_DELETED_SUCCESSFULLY);
    }

    /**
     * Update category details.
     *
     * @param cid     Category ID
     * @param request Update request
     * @return {@link ResultResponse} indicating success or failure
     */
    @Override
    @Transactional
    public ResultResponse<String> updateCategory(String cid, UpdateCategoryRequest request) {
        // Validate input
        if (StrUtil.isBlank(cid)) {
            return ResultResponse.error(ResponseCode.CATEGORY_ID_CANNOT_BE_EMPTY);
        }

        // Find category
        Category category = categoryRepository.findById(cid)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + cid));

        // Check name uniqueness if name is being updated
        if (StrUtil.isNotBlank(request.getName()) && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS);
            }
            category.setName(request.getName());
        }

        // Check slug uniqueness if slug is being updated
        if (StrUtil.isNotBlank(request.getSlug()) && !request.getSlug().equals(category.getSlug())) {
            if (categoryRepository.existsBySlug(request.getSlug())) {
                return ResultResponse.error(ResponseCode.CATEGORY_NAME_EXISTS);
            }
            category.setSlug(request.getSlug());
        }

        // Update description if provided
        if (StrUtil.isNotBlank(request.getDescription())) {
            category.setDescription(request.getDescription());
        }

        // Update parent category if provided
        if (request.getParentId() != null) {
            if (StrUtil.isBlank(request.getParentId())) {
                // Set parent to null (make it a root category)
                category.setParent(null);
            } else {
                // Find and set the new parent
                Category parentCategory = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new RuntimeException(String.format("Parent category not found with ID: %s", request.getParentId())));
                category.setParent(parentCategory);
            }
        }

        // Save changes
        categoryRepository.saveAndFlush(category);

        return ResultResponse.success(ResponseCode.CATEGORY_UPDATED_SUCCESSFULLY);
    }

    /**
     * Get a category by name.
     *
     * @param name category name
     * @return ResultResponse containing the CategoryVO if found
     */
    @Override
    public ResultResponse<CategoryVO> getCategoryByName(String name) {
        if (StrUtil.isBlank(name)) {
            return ResultResponse.error(ResponseCode.CATEGORY_NAME_REQUIRED);
        }

        Category category = categoryRepository.findByName(name);
        if (category == null) {
            return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND);
        }

        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        // Set parent ID for VO
        if (category.getParent() != null) {
            categoryVO.setParentId(category.getParent().getCid());
        }
        return ResultResponse.success(categoryVO);
    }

    /**
     * Get a category by slug.
     *
     * @param slug category slug
     * @return ResultResponse containing the CategoryVO if found
     */
    @Override
    public ResultResponse<CategoryVO> getCategoryBySlug(String slug) {
        if (StrUtil.isBlank(slug)) {
            return ResultResponse.error(ResponseCode.CATEGORY_SLUG_REQUIRED);
        }

        Category category = categoryRepository.findBySlug(slug);
        if (category == null) {
            return ResultResponse.error(ResponseCode.CATEGORY_NOT_FOUND);
        }

        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        // Set parent ID for VO
        if (category.getParent() != null) {
            categoryVO.setParentId(category.getParent().getCid());
        }
        return ResultResponse.success(categoryVO);
    }

    /**
     * Generate a slug from a name by converting to lowercase and replacing spaces with hyphens.
     *
     * @param name The original name
     * @return Generated slug
     */
    private String generateSlugFromName(String name) {
        if (StrUtil.isBlank(name)) {
            return "";
        }
        return name.trim().toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
    }
}