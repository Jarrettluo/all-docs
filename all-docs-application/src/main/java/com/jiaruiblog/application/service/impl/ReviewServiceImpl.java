package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ReviewService;
import com.jiaruiblog.domain.entity.DocReview;
import com.jiaruiblog.infrastructure.repository.DocReviewRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author luojiarui
 **/
@Service
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private DocReviewRepository reviewRepository;
}