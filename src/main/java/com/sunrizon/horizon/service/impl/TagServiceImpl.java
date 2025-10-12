package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.sunrizon.horizon.dto.CreateTagRequest;
import com.sunrizon.horizon.dto.UpdateTagRequest;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Tag;
import com.sunrizon.horizon.repository.TagRepository;
import com.sunrizon.horizon.service.ITagService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.TagVO;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ITagService.
 * <p>
 * Handles tag management operations including creation, update, deletion,
 * and retrieval.
 */
@Service
@Slf4j
public class TagServiceImpl implements ITagService {

  @Resource
  private TagRepository tagRepository;

  /**
   * Create a new tag.
   *
   * Validates uniqueness, and saves tag.
   *
   * @param request Tag creation request
   * @return {@link ResultResponse} with created {@link TagVO}
   */
  @Override
  @Transactional
  public ResultResponse<TagVO> createTag(CreateTagRequest request) {
    // Check name uniqueness
    if (tagRepository.existsByName(request.getName())) {
      return ResultResponse.error(ResponseCode.TAG_NAME_EXISTS);
    }

    // Check slug uniqueness if provided
    if (StrUtil.isNotBlank(request.getSlug()) && tagRepository.existsBySlug(request.getSlug())) {
      return ResultResponse.error(ResponseCode.TAG_SLUG_EXISTS);
    }

    Tag tag = BeanUtil.copyProperties(request, Tag.class);

    // If slug is not provided, generate it from name
    if (StrUtil.isBlank(tag.getSlug())) {
      tag.setSlug(generateSlugFromName(request.getName()));
    }

    // Save tag
    Tag savedTag = tagRepository.save(tag);

    // Convert to VO and return
    TagVO tagVO = BeanUtil.copyProperties(savedTag, TagVO.class);
    return ResultResponse.success(ResponseCode.TAG_CREATED, tagVO);
  }

  /**
   * Get a tag by ID.
   *
   * @param tid Tag ID
   * @return {@link ResultResponse} with {@link TagVO} or error
   */
  @Override
  public ResultResponse<TagVO> getTag(String tid) {
    // Validate input
    if (StrUtil.isBlank(tid)) {
      return ResultResponse.error(ResponseCode.TAG_ID_CANNOT_BE_EMPTY);
    }

    // Load tag by ID
    Tag tag = tagRepository.findById(tid)
        .orElseThrow(() -> new RuntimeException("Tag not found with tid: " + tid));

    // Map entity to VO
    TagVO tagVO = BeanUtil.copyProperties(tag, TagVO.class);

    // Return response
    return ResultResponse.success(tagVO);
  }

  /**
   * Get a paginated list of tags.
   *
   * @param pageable Pagination and sorting info
   * @return {@link ResultResponse} with paginated {@link TagVO} list
   */
  @Override
  public ResultResponse<List<TagVO>> getTags(Pageable pageable) {
    // Fetch paginated tags
    Page<Tag> tagPage = tagRepository.findAll(pageable);

    // Map entity to VO list
    List<TagVO> voList = tagPage.getContent().stream()
        .map(tag -> BeanUtil.copyProperties(tag, TagVO.class))
        .collect(java.util.stream.Collectors.toList());

    // Return response
    return ResultResponse.success(voList);
  }

  /**
   * Delete a tag by ID.
   *
   * @param tid Tag ID
   * @return {@link ResultResponse} with success or error message
   */
  @Override
  @Transactional
  public ResultResponse<String> deleteTag(String tid) {
    // Validate input
    if (StrUtil.isBlank(tid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Tag ID is required");
    }

    // Find tag
    Tag tag = tagRepository.findById(tid)
        .orElseThrow(() -> new RuntimeException("Tag not found with ID: " + tid));

    // Delete tag
    tagRepository.delete(tag);

    return ResultResponse.success(ResponseCode.TAG_DELETED_SUCCESSFULLY);
  }

  /**
   * Update tag details.
   *
   * @param tid     Tag ID
   * @param request Update request
   * @return {@link ResultResponse} indicating success or failure
   */
  @Override
  @Transactional
  public ResultResponse<String> updateTag(String tid, UpdateTagRequest request) {
    // Validate input
    if (StrUtil.isBlank(tid)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST, "Tag ID is required");
    }

    // Find tag
    Tag tag = tagRepository.findById(tid)
        .orElseThrow(() -> new RuntimeException("Tag not found with ID: " + tid));

    // Check name uniqueness if name is being updated
    if (StrUtil.isNotBlank(request.getName()) && !request.getName().equals(tag.getName())) {
      if (tagRepository.existsByName(request.getName())) {
        return ResultResponse.error(ResponseCode.TAG_NAME_EXISTS);
      }
      tag.setName(request.getName());
    }

    // Check slug uniqueness if slug is being updated
    if (StrUtil.isNotBlank(request.getSlug()) && !request.getSlug().equals(tag.getSlug())) {
      if (tagRepository.existsBySlug(request.getSlug())) {
        return ResultResponse.error(ResponseCode.TAG_SLUG_EXISTS);
      }
      tag.setSlug(request.getSlug());
    }

    // Update description if provided
    if (StrUtil.isNotBlank(request.getDescription())) {
      tag.setDescription(request.getDescription());
    }

    // Save changes
    tagRepository.saveAndFlush(tag);

    return ResultResponse.success(ResponseCode.TAG_UPDATED_SUCCESSFULLY);
  }

  /**
   * Get a tag by name.
   *
   * @param name tag name
   * @return ResultResponse containing the TagVO if found
   */
  @Override
  public ResultResponse<TagVO> getTagByName(String name) {
    if (StrUtil.isBlank(name)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Tag tag = tagRepository.findByName(name);
    if (tag == null) {
      return ResultResponse.error(ResponseCode.TAG_NOT_FOUND);
    }

    TagVO tagVO = BeanUtil.copyProperties(tag, TagVO.class);
    return ResultResponse.success(tagVO);
  }

  /**
   * Get a tag by slug.
   *
   * @param slug tag slug
   * @return ResultResponse containing the TagVO if found
   */
  @Override
  public ResultResponse<TagVO> getTagBySlug(String slug) {
    if (StrUtil.isBlank(slug)) {
      return ResultResponse.error(ResponseCode.BAD_REQUEST);
    }

    Tag tag = tagRepository.findBySlug(slug);
    if (tag == null) {
      return ResultResponse.error(ResponseCode.TAG_NOT_FOUND);
    }

    TagVO tagVO = BeanUtil.copyProperties(tag, TagVO.class);
    return ResultResponse.success(tagVO);
  }

  /**
   * Get all tags (non-paginated).
   *
   * @return {@link ResultResponse} with list of all {@link TagVO}
   */
  @Override
  public ResultResponse<List<TagVO>> getAllTags() {
    // Fetch all tags
    List<Tag> tags = tagRepository.findAll();

    // Map entity to VO list
    List<TagVO> voList = tags.stream()
        .map(tag -> BeanUtil.copyProperties(tag, TagVO.class))
        .collect(java.util.stream.Collectors.toList());

    // Return response
    return ResultResponse.success(voList);
  }

  /**
   * Generate a slug from a name by converting to lowercase and replacing spaces
   * with hyphens.
   *
   * @param name The original name
   * @return Generated slug
   */
  private String generateSlugFromName(String name) {
    if (StrUtil.isBlank(name)) {
      return \"\";
    }
    return name.trim().toLowerCase().replaceAll(\"[^a-z0-9\\\\s-]\", \"\").replaceAll(\"\\\\s+\", \"-\");
  }
}
