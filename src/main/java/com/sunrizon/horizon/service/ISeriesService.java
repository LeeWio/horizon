package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateSeriesRequest;
import com.sunrizon.horizon.dto.UpdateSeriesRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.SeriesVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISeriesService {

    /**
     * Create a new series.
     *
     * @param request DTO with series creation info
     * @return ResultResponse containing the created SeriesVO
     */
    ResultResponse<SeriesVO> createSeries(CreateSeriesRequest request);

    /**
     * Get a series by ID.
     *
     * @param sid unique series ID
     * @return ResultResponse containing the SeriesVO if found
     */
    ResultResponse<SeriesVO> getSeries(String sid);

    /**
     * Get a paginated list of series.
     *
     * @param pageable pagination info (page number, size, sort)
     * @return ResultResponse containing a page of SeriesVO
     */
    ResultResponse<Page<SeriesVO>> getSerieses(Pageable pageable);

    /**
     * Delete a series by ID.
     *
     * @param sid unique series ID
     * @return ResultResponse with success or error message
     */
    ResultResponse<String> deleteSeries(String sid);

    /**
     * Update a series's information.
     *
     * @param sid     unique series ID
     * @param request DTO with updated series fields
     * @return ResultResponse with success or error message
     */
    ResultResponse<String> updateSeries(String sid, UpdateSeriesRequest request);

    /**
     * Get a series by name.
     *
     * @param name series name
     * @return ResultResponse containing the SeriesVO if found
     */
    ResultResponse<SeriesVO> getSeriesByName(String name);

    /**
     * Get a series by slug.
     *
     * @param slug series slug
     * @return ResultResponse containing the SeriesVO if found
     */
    ResultResponse<SeriesVO> getSeriesBySlug(String slug);
}