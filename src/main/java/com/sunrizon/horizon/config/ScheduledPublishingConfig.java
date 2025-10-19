package com.sunrizon.horizon.config;

import com.sunrizon.horizon.service.IScheduledPublishingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class ScheduledPublishingConfig {
    
    @Resource
    private IScheduledPublishingService scheduledPublishingService;
    
    /**
     * Check for scheduled articles to publish every minute.
     */
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void publishScheduledArticles() {
        log.debug("Checking for scheduled articles to publish...");
        scheduledPublishingService.publishScheduledArticles();
    }
}