package com.sunrizon.horizon.service;

import com.sunrizon.horizon.utils.ResultResponse;

public interface IScheduledPublishingService {
    
    /**
     * Publish articles that are scheduled and due for publishing.
     * 
     * @return ResultResponse with the number of articles published
     */
    ResultResponse<Integer> publishScheduledArticles();
}