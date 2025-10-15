package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateCategoryRequest;
import com.sunrizon.horizon.dto.UpdateCategoryRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.CategoryVO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {

    /**
     * Create a new category.
     *
     * @param request DTO with category creation info
     * @return ResultResponse containing the created CategoryVO
     */
    ResultResponse<CategoryVO> createCategory(CreateCategoryRequest request);

    /**
     * Get a category by ID.
     *
     * @param id unique category ID
     * @return ResultResponse containing the CategoryVO if found
     */
    ResultResponse<CategoryVO> getCategory(String id);

    /**
     * Get a paginated list of categories.
     *
     * @param pageable pagination info (page number, size, sort)
     * @return ResultResponse containing a page of CategoryVO
     */
    ResultResponse<Page<CategoryVO>> getCategories(Pageable pageable);

    /**
     * Delete a category by ID.
     *
     * @param id unique category ID
     * @return ResultResponse with success or error message
     */
    ResultResponse<String> deleteCategory(String id);

    /**
     * Update a category's information.
     *
     * @param id     unique category ID
     * @param request DTO with updated category fields
     * @return ResultResponse with success or error message
     */
    ResultResponse<String> updateCategory(String id, UpdateCategoryRequest request);

    /**
     * Get a category by name.
     *
     * @param name category name
     * @return ResultResponse containing the CategoryVO if found
     */
    ResultResponse<CategoryVO> getCategoryByName(String name);

    /**
   * Get a category by slug.
   *
   * @param slug category slug
   * @return ResultResponse containing the CategoryVO if found
   */
  ResultResponse<CategoryVO> getCategoryBySlug(String slug);

  /**
   * Get all categories (non-paginated).
   *
   * @return ResultResponse containing a list of all CategoryVO
   */
  ResultResponse<List<CategoryVO>> getAllCategories();
}
