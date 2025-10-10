package com.sunrizon.horizon.controller;

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

import com.sunrizon.horizon.dto.CreateSeriesRequest;
import com.sunrizon.horizon.dto.UpdateSeriesRequest;
import com.sunrizon.horizon.service.ISeriesService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.SeriesVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/series")
@Slf4j
public class SeriesController {

    @Resource
    private ISeriesService seriesService;

    /**
     * Create a new series.
     *
     * @param request DTO containing series creation data
     * @return ResultResponse wrapping the created SeriesVO
     */
    @PostMapping("/create")
    public ResultResponse<SeriesVO> createSeries(@Valid @RequestBody CreateSeriesRequest request) {
        return seriesService.createSeries(request);
    }

    /**
     * Retrieve a series by their unique ID.
     *
     * @param sid Unique identifier of the series
     * @return ResultResponse wrapping SeriesVO if found, or error if not found
     */
    @GetMapping("/{sid}")
    public ResultResponse<SeriesVO> getSeries(@PathVariable("sid") String sid) {
        return seriesService.getSeries(sid);
    }

    /**
     * Retrieve a series by name.
     *
     * @param name Name of the series
     * @return ResultResponse wrapping SeriesVO if found, or error if not found
     */
    @GetMapping("/name/{name}")
    public ResultResponse<SeriesVO> getSeriesByName(@PathVariable("name") String name) {
        return seriesService.getSeriesByName(name);
    }

    /**
     * Retrieve a series by slug.
     *
     * @param slug Slug of the series
     * @return ResultResponse wrapping SeriesVO if found, or error if not found
     */
    @GetMapping("/slug/{slug}")
    public ResultResponse<SeriesVO> getSeriesBySlug(@PathVariable("slug") String slug) {
        return seriesService.getSeriesBySlug(slug);
    }

    @DeleteMapping("/{sid}")
    public ResultResponse<String> deleteSeries(@PathVariable("sid") String sid) {
        return seriesService.deleteSeries(sid);
    }

    @PutMapping("/{sid}")
    public ResultResponse<String> updateSeries(@PathVariable("sid") String sid, 
                                              @Valid @RequestBody UpdateSeriesRequest request) {
        return seriesService.updateSeries(sid, request);
    }

    @GetMapping
    public ResultResponse<Page<SeriesVO>> getSerieses(Pageable pageable) {
        return seriesService.getSerieses(pageable);
    }

}