package com.sunrizon.horizon.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunrizon.horizon.dto.CreateArticleRequest;
import com.sunrizon.horizon.service.IArticleService;
import com.sunrizon.horizon.utils.ResultResponse;
import com.sunrizon.horizon.vo.ArticleVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/article")
@Slf4j
public class ArticleController {

    @Resource
    private IArticleService articleService;

    /**
     * Create a new article.
     *
     * @param request DTO containing article creation data
     * @return ResultResponse wrapping the created ArticleVO
     */
    @PostMapping("/create")
    public ResultResponse<ArticleVO> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        return articleService.createArticle(request);
    }

}