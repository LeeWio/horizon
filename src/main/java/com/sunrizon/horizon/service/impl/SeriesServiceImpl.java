package com.sunrizon.horizon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.sunrizon.horizon.dto.CreateSeriesRequest;
import com.sunrizon.horizon.dto.UpdateSeriesRequest;
import com.sunrizon.horizon.enums.SeriesStatus;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Series;
import com.sunrizon.horizon.repository.SeriesRepository;
import com.sunrizon.horizon.service.ISeriesService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.SeriesVO;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ISeriesService.
 * <p>
 * Handles series management operations including creation, update, deletion,
 * and retrieval.
 */
@Service
@Slf4j
public class SeriesServiceImpl implements ISeriesService {

    @Resource
    private SeriesRepository seriesRepository;

    /**
     * Create a new series.
     *
     * Validates uniqueness, and saves series.
     *
     * @param request Series creation request
     * @return {@link ResultResponse} with created {@link SeriesVO}
     */
    @Override
    @Transactional
    public ResultResponse<SeriesVO> createSeries(CreateSeriesRequest request) {
        // Check name uniqueness
        if (seriesRepository.existsByName(request.getName())) {
            return ResultResponse.error(ResponseCode.SERIES_NAME_EXISTS);
        }

        // Check slug uniqueness if provided
        if (StrUtil.isNotBlank(request.getSlug()) && seriesRepository.existsBySlug(request.getSlug())) {
            return ResultResponse.error(ResponseCode.SERIES_NAME_EXISTS);
        }

        // Map DTO to Entity
        Series series = new Series();
        series.setName(request.getName());
        series.setSlug(request.getSlug());
        series.setDescription(request.getDescription());
        series.setCoverImage(request.getCoverImage());
        series.setStatus(request.getStatus() != null ? request.getStatus() : SeriesStatus.DRAFT);
        series.setAuthorId(request.getAuthorId());

        // If slug is not provided, generate it from name
        if (StrUtil.isBlank(series.getSlug())) {
            series.setSlug(generateSlugFromName(request.getName()));
        }

        // Save series
        Series savedSeries = seriesRepository.save(series);

        // Convert to VO and return
        SeriesVO seriesVO = BeanUtil.copyProperties(savedSeries, SeriesVO.class);
        return ResultResponse.success(ResponseCode.SERIES_CREATED, seriesVO);
    }

    /**
     * Get a series by ID.
     *
     * @param sid Series ID
     * @return {@link ResultResponse} with {@link SeriesVO} or error
     */
    @Override
    public ResultResponse<SeriesVO> getSeries(String sid) {
        // Validate input
        if (StrUtil.isBlank(sid)) {
            return ResultResponse.error(ResponseCode.SERIES_ID_CANNOT_BE_EMPTY);
        }

        // Load series by ID
        Series series = seriesRepository.findById(sid)
                .orElseThrow(() -> new RuntimeException("Series not found with sid: " + sid));

        // Map entity to VO
        SeriesVO seriesVO = BeanUtil.copyProperties(series, SeriesVO.class);

        // Return response
        return ResultResponse.success(seriesVO);
    }

    /**
     * Get a paginated list of series.
     *
     * @param pageable Pagination and sorting info
     * @return {@link ResultResponse} with paginated {@link SeriesVO} list
     */
    @Override
    public ResultResponse<Page<SeriesVO>> getSerieses(Pageable pageable) {
        // Fetch paginated series
        Page<Series> seriesPage = seriesRepository.findAll(pageable);

        // Map entity to VO
        Page<SeriesVO> voPage = seriesPage.map(series -> BeanUtil.copyProperties(series, SeriesVO.class));

        // Return response
        return ResultResponse.success(voPage);
    }

    /**
     * Delete a series by ID.
     *
     * @param sid Series ID
     * @return {@link ResultResponse} with success or error message
     */
    @Override
    @Transactional
    public ResultResponse<String> deleteSeries(String sid) {
        // Validate input
        if (StrUtil.isBlank(sid)) {
            return ResultResponse.error(ResponseCode.SERIES_ID_CANNOT_BE_EMPTY);
        }

        // Find series
        Series series = seriesRepository.findById(sid)
                .orElseThrow(() -> new RuntimeException("Series not found with ID: " + sid));

        // Delete series
        seriesRepository.delete(series);

        return ResultResponse.success(ResponseCode.SERIES_DELETED_SUCCESSFULLY);
    }

    /**
     * Update series details.
     *
     * @param sid     Series ID
     * @param request Update request
     * @return {@link ResultResponse} indicating success or failure
     */
    @Override
    @Transactional
    public ResultResponse<String> updateSeries(String sid, UpdateSeriesRequest request) {
        // Validate input
        if (StrUtil.isBlank(sid)) {
            return ResultResponse.error(ResponseCode.SERIES_ID_CANNOT_BE_EMPTY);
        }

        // Find series
        Series series = seriesRepository.findById(sid)
                .orElseThrow(() -> new RuntimeException("Series not found with ID: " + sid));

        // Check name uniqueness if name is being updated
        if (StrUtil.isNotBlank(request.getName()) && !request.getName().equals(series.getName())) {
            if (seriesRepository.existsByName(request.getName())) {
                return ResultResponse.error(ResponseCode.SERIES_NAME_EXISTS);
            }
            series.setName(request.getName());
        }

        // Check slug uniqueness if slug is being updated
        if (StrUtil.isNotBlank(request.getSlug()) && !request.getSlug().equals(series.getSlug())) {
            if (seriesRepository.existsBySlug(request.getSlug())) {
                return ResultResponse.error(ResponseCode.SERIES_NAME_EXISTS);
            }
            series.setSlug(request.getSlug());
        }

        // Update other fields if provided
        if (StrUtil.isNotBlank(request.getDescription())) {
            series.setDescription(request.getDescription());
        }
        if (StrUtil.isNotBlank(request.getCoverImage())) {
            series.setCoverImage(request.getCoverImage());
        }
        if (request.getStatus() != null) {
            series.setStatus(request.getStatus());
        }

        // Save changes
        seriesRepository.saveAndFlush(series);

        return ResultResponse.success(ResponseCode.SERIES_UPDATED_SUCCESSFULLY);
    }

    /**
     * Get a series by name.
     *
     * @param name series name
     * @return ResultResponse containing the SeriesVO if found
     */
    @Override
    public ResultResponse<SeriesVO> getSeriesByName(String name) {
        if (StrUtil.isBlank(name)) {
            return ResultResponse.error(ResponseCode.SERIES_NAME_REQUIRED);
        }

        Series series = seriesRepository.findByName(name);
        if (series == null) {
            return ResultResponse.error(ResponseCode.SERIES_NOT_FOUND);
        }

        SeriesVO seriesVO = BeanUtil.copyProperties(series, SeriesVO.class);
        return ResultResponse.success(seriesVO);
    }

    /**
     * Get a series by slug.
     *
     * @param slug series slug
     * @return ResultResponse containing the SeriesVO if found
     */
    @Override
    public ResultResponse<SeriesVO> getSeriesBySlug(String slug) {
        if (StrUtil.isBlank(slug)) {
            return ResultResponse.error(ResponseCode.SERIES_SLUG_REQUIRED);
        }

        Series series = seriesRepository.findBySlug(slug);
        if (series == null) {
            return ResultResponse.error(ResponseCode.SERIES_NOT_FOUND);
        }

        SeriesVO seriesVO = BeanUtil.copyProperties(series, SeriesVO.class);
        return ResultResponse.success(seriesVO);
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