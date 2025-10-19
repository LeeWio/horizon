package com.sunrizon.horizon.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.sunrizon.horizon.enums.ArticleStatus;
import com.sunrizon.horizon.enums.ResponseCode;
import com.sunrizon.horizon.pojo.Article;
import com.sunrizon.horizon.repository.ArticleRepository;
import com.sunrizon.horizon.service.IScheduledPublishingService;
import com.sunrizon.horizon.utils.ResultResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ScheduledPublishingServiceImpl implements IScheduledPublishingService {
    
    @Resource
    private ArticleRepository articleRepository;
    
    @Override
    @Transactional
    public ResultResponse<Integer> publishScheduledArticles() {
        try {
            // Get current time
            LocalDateTime now = LocalDateTime.now();
            
            // Find all scheduled articles that are due for publishing
            List<Article> scheduledArticles = articleRepository.findByStatusAndPublishTimeLessThanEqual(
                ArticleStatus.SCHEDULED, now);
            
            // Publish each article
            int publishedCount = 0;
            for (Article article : scheduledArticles) {
                article.setStatus(ArticleStatus.PUBLISHED);
                article.setUpdatedAt(now);
                articleRepository.save(article);
                publishedCount++;
                log.info("Published scheduled article: {} (ID: {})", article.getTitle(), article.getAid());
            }
            
            log.info("Published {} scheduled articles at {}", publishedCount, now);
            return ResultResponse.success(ResponseCode.SUCCESS, publishedCount);
        } catch (Exception e) {
            log.error("Error publishing scheduled articles", e);
            return ResultResponse.error(ResponseCode.INTERNAL_ERROR);
        }
    }
}