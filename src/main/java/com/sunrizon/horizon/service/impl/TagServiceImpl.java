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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Service
@Slf4j
public class TagServiceImpl implements ITagService {

    @Resource
    private TagRepository tagRepository;

    @Override
    @Transactional
    public ResultResponse<TagVO> createTag(CreateTagRequest request) {
        log.info("创建标签请求: name={}", request.getName());

        // 1. 验证标签名称是否已存在
        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            return ResultResponse.error(ResponseCode.TAG_NAME_EXISTS, "标签名称已存在");
        }

        // 2. 验证标签别名是否已存在（如果提供了别名）
        if (StrUtil.isNotBlank(request.getSlug()) && tagRepository.existsBySlugIgnoreCase(request.getSlug())) {
            return ResultResponse.error(ResponseCode.TAG_NAME_EXISTS, "标签别名已存在");
        }

        // 3. 创建标签实体
        Tag tag = BeanUtil.copyProperties(request, Tag.class);
        tag.setArticleCount(0L);

        // 4. 保存标签
        Tag savedTag = tagRepository.save(tag);

        // 5. 转换为VO
        TagVO tagVO = convertToVO(savedTag);

        log.info("标签创建成功: tid={}, name={}", savedTag.getTid(), savedTag.getName());
        return ResultResponse.success("标签创建成功", tagVO);
    }

    @Override
    @Transactional
    public ResultResponse<TagVO> updateTag(String tid, UpdateTagRequest request) {
        log.info("更新标签请求: tid={}", tid);

        // 1. 查找标签
        Tag tag = tagRepository.findById(tid).orElse(null);
        if (tag == null) {
            return ResultResponse.error(ResponseCode.TAG_NOT_FOUND, "标签不存在");
        }

        // 2. 验证标签名称是否已存在（如果修改了名称）
        if (StrUtil.isNotBlank(request.getName()) && !request.getName().equals(tag.getName())) {
            if (tagRepository.existsByNameIgnoreCase(request.getName())) {
                return ResultResponse.error(ResponseCode.TAG_NAME_EXISTS, "标签名称已存在");
            }
        }

        // 3. 验证标签别名是否已存在（如果修改了别名）
        if (StrUtil.isNotBlank(request.getSlug()) && !request.getSlug().equals(tag.getSlug())) {
            if (tagRepository.existsBySlugIgnoreCase(request.getSlug())) {
                return ResultResponse.error(ResponseCode.TAG_NAME_EXISTS, "标签别名已存在");
            }
        }

        // 4. 更新标签属性
        if (StrUtil.isNotBlank(request.getName())) {
            tag.setName(request.getName());
        }
        if (StrUtil.isNotBlank(request.getSlug())) {
            tag.setSlug(request.getSlug());
        }
        if (StrUtil.isNotBlank(request.getDescription())) {
            tag.setDescription(request.getDescription());
        }
        if (StrUtil.isNotBlank(request.getColor())) {
            tag.setColor(request.getColor());
        }
        if (request.getIsActive() != null) {
            tag.setIsActive(request.getIsActive());
        }

        // 5. 保存更新
        Tag updatedTag = tagRepository.save(tag);

        // 6. 转换为VO
        TagVO tagVO = convertToVO(updatedTag);

        log.info("标签更新成功: tid={}, name={}", updatedTag.getTid(), updatedTag.getName());
        return ResultResponse.success("标签更新成功", tagVO);
    }

    @Override
    @Transactional
    public ResultResponse<String> deleteTag(String tid) {
        log.info("删除标签请求: tid={}", tid);

        // 1. 查找标签
        Tag tag = tagRepository.findById(tid).orElse(null);
        if (tag == null) {
            return ResultResponse.error(ResponseCode.TAG_NOT_FOUND, "标签不存在");
        }

        // 2. 检查是否有文章使用该标签
        if (tag.getArticleCount() > 0) {
            return ResultResponse.error(ResponseCode.TAG_NAME_EXISTS, "标签下存在文章，无法删除");
        }

        // 3. 删除标签
        tagRepository.delete(tag);

        log.info("标签删除成功: tid={}, name={}", tid, tag.getName());
        return ResultResponse.success("标签删除成功");
    }

    @Override
    public ResultResponse<TagVO> getTag(String tid) {
        log.info("获取标签请求: tid={}", tid);

        Tag tag = tagRepository.findById(tid).orElse(null);
        if (tag == null) {
            return ResultResponse.error(ResponseCode.TAG_NOT_FOUND, "标签不存在");
        }

        TagVO tagVO = convertToVO(tag);
        return ResultResponse.success(tagVO);
    }

    @Override
    public ResultResponse<List<TagVO>> getActiveTags() {
        log.info("获取激活标签列表");

        List<Tag> tags = tagRepository.findByIsActiveTrueOrderByArticleCountDesc();
        List<TagVO> tagVOs = tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(tagVOs);
    }

    @Override
    public ResultResponse<Page<TagVO>> getTags(Pageable pageable) {
        log.info("获取标签分页数据: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Tag> tags = tagRepository.findAll(pageable);
        Page<TagVO> tagVOs = tags.map(this::convertToVO);

        return ResultResponse.success(tagVOs);
    }

    @Override
    public ResultResponse<Page<TagVO>> getTagsByStatus(Boolean isActive, Pageable pageable) {
        log.info("根据状态获取标签: isActive={}", isActive);

        Page<Tag> tags = tagRepository.findByIsActiveOrderByArticleCountDesc(isActive, pageable);
        Page<TagVO> tagVOs = tags.map(this::convertToVO);

        return ResultResponse.success(tagVOs);
    }

    @Override
    public ResultResponse<Page<TagVO>> searchTagsByName(String name, Pageable pageable) {
        log.info("搜索标签: name={}", name);

        Page<Tag> tags = tagRepository.findByNameContainingIgnoreCase(name, pageable);
        Page<TagVO> tagVOs = tags.map(this::convertToVO);

        return ResultResponse.success(tagVOs);
    }

    @Override
    @Transactional
    public ResultResponse<String> updateTagStatus(String tid, Boolean isActive) {
        log.info("更新标签状态: tid={}, isActive={}", tid, isActive);

        Tag tag = tagRepository.findById(tid).orElse(null);
        if (tag == null) {
            return ResultResponse.error(ResponseCode.TAG_NOT_FOUND, "标签不存在");
        }

        tag.setIsActive(isActive);
        tagRepository.save(tag);

        log.info("标签状态更新成功: tid={}, isActive={}", tid, isActive);
        return ResultResponse.success("标签状态更新成功");
    }

    @Override
    public ResultResponse<List<TagVO>> getPopularTags(int limit) {
        log.info("获取热门标签: limit={}", limit);

        List<Tag> tags = tagRepository.findTopTagsByArticleCount(limit);
        List<TagVO> tagVOs = tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(tagVOs);
    }

    @Override
    public ResultResponse<Page<TagVO>> getPopularTags(Pageable pageable) {
        log.info("获取热门标签分页数据");

        Page<Tag> tags = tagRepository.findTopTagsByArticleCount(pageable);
        Page<TagVO> tagVOs = tags.map(this::convertToVO);

        return ResultResponse.success(tagVOs);
    }

    @Override
    public ResultResponse<Page<TagVO>> getTagsByArticleCountRange(Long minCount, Long maxCount, Pageable pageable) {
        log.info("根据文章数量范围获取标签: minCount={}, maxCount={}", minCount, maxCount);

        Page<Tag> tags = tagRepository.findByArticleCountBetween(minCount, maxCount, pageable);
        Page<TagVO> tagVOs = tags.map(this::convertToVO);

        return ResultResponse.success(tagVOs);
    }

    @Override
    public ResultResponse<List<TagVO>> getTagsByIds(List<String> tagIds) {
        log.info("根据ID列表获取标签: tagIds={}", tagIds);

        List<Tag> tags = tagRepository.findByIds(tagIds);
        List<TagVO> tagVOs = tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return ResultResponse.success(tagVOs);
    }

    @Override
    public ResultResponse<Boolean> checkTagNameExists(String name) {
        boolean exists = tagRepository.existsByNameIgnoreCase(name);
        return ResultResponse.success(exists);
    }

    @Override
    public ResultResponse<Boolean> checkTagSlugExists(String slug) {
        boolean exists = tagRepository.existsBySlugIgnoreCase(slug);
        return ResultResponse.success(exists);
    }

    /**
     * 转换为VO
     */
    private TagVO convertToVO(Tag tag) {
        return BeanUtil.copyProperties(tag, TagVO.class);
    }
}
