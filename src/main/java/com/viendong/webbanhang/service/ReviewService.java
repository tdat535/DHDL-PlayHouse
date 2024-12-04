package com.viendong.webbanhang.service;

import com.viendong.webbanhang.model.Reviews;
import com.viendong.webbanhang.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Reviews> getReviewsByProductId(Long productId) {
        List<Reviews> reviews = reviewRepository.findByProductId(productId);
        return (reviews != null) ? reviews : new ArrayList<>();
    }


    public Reviews addReview(Reviews review) {
        return reviewRepository.save(review);
    }

}
