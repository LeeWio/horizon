package com.sunrizon.horizon.service;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;

public interface IArticleService {

    /**
     * Create a new article.
     *
     * @param request DTO with article creation info
     * @return ResultResponse containing the created ArticleVO
     */
    ResultResponse<ArticleVO> createArticle(CreateArticleRequest request);

}