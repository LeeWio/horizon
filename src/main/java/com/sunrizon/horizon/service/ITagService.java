package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateTagRequest;
import com.sunrizon.horizon.dto.UpdateTagRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.TagVO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITagService {

  /**
   * Create a new tag.
   *
   * @param request DTO with tag creation info
   * @return ResultResponse containing the created TagVO
   */
  ResultResponse<TagVO> createTag(CreateTagRequest request);

  /**
   * Get a tag by ID.
   *
   * @param tid unique tag ID
   * @return ResultResponse containing the TagVO if found
   */
  ResultResponse<TagVO> getTag(String tid);

  /**
   * Get a paginated list of tags.
   *
   * @param pageable pagination info (page number, size, sort)
   * @return ResultResponse containing a page of TagVO
   */
  ResultResponse<List<TagVO>> getTags(Pageable pageable);
  
  /**
   * Get all tags (non-paginated).
   *
   * @return ResultResponse containing a list of all TagVO
   */
  ResultResponse<List<TagVO>> getAllTags();

  /**
   * Delete a tag by ID.
   *
   * @param tid unique tag ID
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> deleteTag(String tid);

  /**
   * Update a tag's information.
   *
   * @param tid     unique tag ID
   * @param request DTO with updated tag fields
   * @return ResultResponse with success or error message
   */
  ResultResponse<String> updateTag(String tid, UpdateTagRequest request);

  /**
   * Get a tag by name.
   *
   * @param name tag name
   * @return ResultResponse containing the TagVO if found
   */
  ResultResponse<TagVO> getTagByName(String name);

  /**
   * Get a tag by slug.
   *
   * @param slug tag slug
   * @return ResultResponse containing the TagVO if found
   */
  ResultResponse<TagVO> getTagBySlug(String slug);
}
