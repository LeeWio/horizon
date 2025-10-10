package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateColumnRequest;
import com.sunrizon.horizon.dto.UpdateColumnRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ColumnVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IColumnService {

    /**
     * Create a new column.
     *
     * @param request DTO with column creation info
     * @return ResultResponse containing the created ColumnVO
     */
    ResultResponse<ColumnVO> createColumn(CreateColumnRequest request);

    /**
     * Get a column by ID.
     *
     * @param cid unique column ID
     * @return ResultResponse containing the ColumnVO if found
     */
    ResultResponse<ColumnVO> getColumn(String cid);

    /**
     * Get a paginated list of columns.
     *
     * @param pageable pagination info (page number, size, sort)
     * @return ResultResponse containing a page of ColumnVO
     */
    ResultResponse<Page<ColumnVO>> getColumns(Pageable pageable);

    /**
     * Delete a column by ID.
     *
     * @param cid unique column ID
     * @return ResultResponse with success or error message
     */
    ResultResponse<String> deleteColumn(String cid);

    /**
     * Update a column's information.
     *
     * @param cid     unique column ID
     * @param request DTO with updated column fields
     * @return ResultResponse with success or error message
     */
    ResultResponse<String> updateColumn(String cid, UpdateColumnRequest request);

    /**
     * Get a column by name.
     *
     * @param name column name
     * @return ResultResponse containing the ColumnVO if found
     */
    ResultResponse<ColumnVO> getColumnByName(String name);

    /**
     * Get a column by slug.
     *
     * @param slug column slug
     * @return ResultResponse containing the ColumnVO if found
     */
    ResultResponse<ColumnVO> getColumnBySlug(String slug);
}