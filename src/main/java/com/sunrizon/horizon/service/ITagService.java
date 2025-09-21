package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateTagRequest;
import com.sunrizon.horizon.dto.UpdateTagRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.TagVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 标签服务接口
 * 提供标签相关的业务操作
 */
public interface ITagService {

    /**
     * 创建标签
     *
     * @param request 创建标签请求
     * @return 创建结果
     */
    ResultResponse<TagVO> createTag(CreateTagRequest request);

    /**
     * 更新标签
     *
     * @param tid     标签ID
     * @param request 更新标签请求
     * @return 更新结果
     */
    ResultResponse<TagVO> updateTag(String tid, UpdateTagRequest request);

    /**
     * 删除标签
     *
     * @param tid 标签ID
     * @return 删除结果
     */
    ResultResponse<String> deleteTag(String tid);

    /**
     * 根据ID获取标签
     *
     * @param tid 标签ID
     * @return 标签信息
     */
    ResultResponse<TagVO> getTag(String tid);

    /**
     * 获取所有激活的标签
     *
     * @return 标签列表
     */
    ResultResponse<List<TagVO>> getActiveTags();

    /**
     * 获取所有标签（分页）
     *
     * @param pageable 分页参数
     * @return 标签分页数据
     */
    ResultResponse<Page<TagVO>> getTags(Pageable pageable);

    /**
     * 根据状态获取标签
     *
     * @param isActive 是否激活
     * @param pageable 分页参数
     * @return 标签分页数据
     */
    ResultResponse<Page<TagVO>> getTagsByStatus(Boolean isActive, Pageable pageable);

    /**
     * 根据名称搜索标签
     *
     * @param name     标签名称
     * @param pageable 分页参数
     * @return 标签分页数据
     */
    ResultResponse<Page<TagVO>> searchTagsByName(String name, Pageable pageable);

    /**
     * 更新标签状态
     *
     * @param tid      标签ID
     * @param isActive 是否激活
     * @return 更新结果
     */
    ResultResponse<String> updateTagStatus(String tid, Boolean isActive);

    /**
     * 获取热门标签
     *
     * @param limit 限制数量
     * @return 热门标签列表
     */
    ResultResponse<List<TagVO>> getPopularTags(int limit);

    /**
     * 获取热门标签（分页）
     *
     * @param pageable 分页参数
     * @return 热门标签分页数据
     */
    ResultResponse<Page<TagVO>> getPopularTags(Pageable pageable);

    /**
     * 根据文章数量范围获取标签
     *
     * @param minCount 最小文章数量
     * @param maxCount 最大文章数量
     * @param pageable 分页参数
     * @return 标签分页数据
     */
    ResultResponse<Page<TagVO>> getTagsByArticleCountRange(Long minCount, Long maxCount, Pageable pageable);

    /**
     * 根据多个标签ID获取标签
     *
     * @param tagIds 标签ID列表
     * @return 标签列表
     */
    ResultResponse<List<TagVO>> getTagsByIds(List<String> tagIds);

    /**
     * 检查标签名称是否存在
     *
     * @param name 标签名称
     * @return 是否存在
     */
    ResultResponse<Boolean> checkTagNameExists(String name);

    /**
     * 检查标签别名是否存在
     *
     * @param slug 标签别名
     * @return 是否存在
     */
    ResultResponse<Boolean> checkTagSlugExists(String slug);
}
