package com.sunrizon.horizon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.CreateCategoryRequest;
import com.sunrizon.horizon.dto.UpdateCategoryRequest;
import com.sunrizon.horizon.service.ICategoryService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.CategoryVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/category")
@Slf4j
public class CategoryController {

  @Resource
  private ICategoryService categoryService;

  /**
   * Create a new category.
   *
   * @param request DTO containing category creation data
   * @return ResultResponse wrapping the created CategoryVO
   */
  @PostMapping("/create")
  public ResultResponse<CategoryVO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
    return categoryService.createCategory(request);
  }

  @GetMapping("/all")
  public ResultResponse<List<CategoryVO>> getAllCategories() {
    return categoryService.getAllCategories();
  }

  /**
   * Retrieve a category by their unique ID.
   *
   * @param cid Unique identifier of the category
   * @return ResultResponse wrapping CategoryVO if found, or error if not found
   */
  @GetMapping("/{cid}")
  public ResultResponse<CategoryVO> getCategory(@PathVariable("cid") String cid) {
    return categoryService.getCategory(cid);
  }

  /**
   * Retrieve a category by name.
   *
   * @param name Name of the category
   * @return ResultResponse wrapping CategoryVO if found, or error if not found
   */
  @GetMapping("/name/{name}")
  public ResultResponse<CategoryVO> getCategoryByName(@PathVariable("name") String name) {
    return categoryService.getCategoryByName(name);
  }

  /**
   * Retrieve a category by slug.
   *
   * @param slug Slug of the category
   * @return ResultResponse wrapping CategoryVO if found, or error if not found
   */
  @GetMapping("/slug/{slug}")
  public ResultResponse<CategoryVO> getCategoryBySlug(@PathVariable("slug") String slug) {
    return categoryService.getCategoryBySlug(slug);
  }

  @DeleteMapping("/{cid}")
  public ResultResponse<String> deleteCategory(@PathVariable("cid") String cid) {
    return categoryService.deleteCategory(cid);
  }

  @PutMapping("/{cid}")
  public ResultResponse<String> updateCategory(@PathVariable("cid") String cid,
      @Valid @RequestBody UpdateCategoryRequest request) {
    return categoryService.updateCategory(cid, request);
  }

  @GetMapping
  public ResultResponse<Page<CategoryVO>> getCategories(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "5") int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return categoryService.getCategories(pageable);
  }

}
