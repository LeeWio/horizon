package com.sunrizon.horizon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.CreateTagRequest;
import com.sunrizon.horizon.dto.UpdateTagRequest;
import com.sunrizon.horizon.service.ITagService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.TagVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tag")
@Slf4j
public class TagController {

  @Resource
  private ITagService tagService;

  /**
   * Create a new tag.
   *
   * @param request DTO containing tag creation data
   * @return ResultResponse wrapping the created TagVO
   */
  @PostMapping("/create")
  public ResultResponse<TagVO> createTag(@Valid @RequestBody CreateTagRequest request) {
    return tagService.createTag(request);
  }

  /**
   * Retrieve a tag by their unique ID.
   *
   * @param tid Unique identifier of the tag
   * @return ResultResponse wrapping TagVO if found, or error if not found
   */
  @GetMapping("/{tid}")
  public ResultResponse<TagVO> getTag(@PathVariable("tid") String tid) {
    return tagService.getTag(tid);
  }

  /**
   * Retrieve a tag by name.
   *
   * @param name Name of the tag
   * @return ResultResponse wrapping TagVO if found, or error if not found
   */
  @GetMapping("/name/{name}")
  public ResultResponse<TagVO> getTagByName(@PathVariable("name") String name) {
    return tagService.getTagByName(name);
  }

  /**
   * Retrieve a tag by slug.
   *
   * @param slug Slug of the tag
   * @return ResultResponse wrapping TagVO if found, or error if not found
   */
  @GetMapping("/slug/{slug}")
  public ResultResponse<TagVO> getTagBySlug(@PathVariable("slug") String slug) {
    return tagService.getTagBySlug(slug);
  }

  @DeleteMapping("/{tid}")
  public ResultResponse<String> deleteTag(@PathVariable("tid") String tid) {
    return tagService.deleteTag(tid);
  }

  @PutMapping("/{tid}")
  public ResultResponse<String> updateTag(@PathVariable("tid") String tid,
      @Valid @RequestBody UpdateTagRequest request) {
    return tagService.updateTag(tid, request);
  }

  @GetMapping
  public ResultResponse<Page<TagVO>> getTags(Pageable pageable) {
    return tagService.getTags(pageable);
  }

  /**
   * Retrieve all tags (non-paginated).
   * 
   * @return ResultResponse wrapping list of all TagVO
   */
  @GetMapping("/all")
  public ResultResponse<List<TagVO>> getAllTags() {
    return tagService.getAllTags();
  }

}
