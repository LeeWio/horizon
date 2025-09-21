package com.sunrizon.horizon.exception;

import com.sunrizon.horizon.enums.ResponseCode;

/**
 * 文章不存在异常
 */
public class ArticleNotFoundException extends BusinessException {
    
    public ArticleNotFoundException() {
        super(ResponseCode.ARTICLE_NOT_FOUND);
    }
    
    public ArticleNotFoundException(String articleId) {
        super(ResponseCode.ARTICLE_NOT_FOUND, "文章不存在，ID: " + articleId, articleId);
    }
    
    public ArticleNotFoundException(String field, String value) {
        super(ResponseCode.ARTICLE_NOT_FOUND, String.format("文章不存在，%s: %s", field, value), field, value);
    }
}
