package com.example.shop.core.review.repository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Optional<ReviewEntity> getReviewByClientAndProduct(UUID clientId, String productCode);
    void createReview(ReviewEntity entity);
    Optional<ReviewEntity> getReviewById(UUID reviewId);
    void updateReview(ReviewEntity entity);
    void deleteReview(UUID reviewId);
}
