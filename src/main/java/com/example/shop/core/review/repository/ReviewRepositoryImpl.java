package com.example.shop.core.review.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.example.shop.public_.tables.Review.REVIEW;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {
    private final DSLContext create;
    private final ReviewEntityMapper reviewEntityMapper = new ReviewEntityMapper();

    @Override
    public Optional<ReviewEntity> getReviewByClientAndProduct(UUID clientId, String productCode) {
        return create.selectFrom(REVIEW)
                .where(REVIEW.CLIENT_ID.eq(clientId))
                .and(REVIEW.PRODUCT_ID.eq(productCode))
                .fetchOptional(reviewEntityMapper);
    }

    @Override
    public void createReview(ReviewEntity entity) {
        create.insertInto(REVIEW)
                .set(REVIEW.REVIEW_ID, entity.reviewId())
                .set(REVIEW.CLIENT_ID, entity.clientId())
                .set(REVIEW.PRODUCT_ID, entity.productCode())
                .set(REVIEW.CREATED_TIME, entity.createdTime())
                .set(REVIEW.MODIFIED_TIME, entity.modifiedTime())
                .set(REVIEW.RATING, entity.rating())
                .set(REVIEW.REVIEW_BODY, JSONB.valueOf(entity.body()))
                .execute();
    }

    @Override
    public Optional<ReviewEntity> getReviewById(UUID reviewId) {
        return create.selectFrom(REVIEW)
                .where(REVIEW.REVIEW_ID.eq(reviewId))
                .fetchOptional(reviewEntityMapper);
    }

    @Override
    public void updateReview(ReviewEntity entity) {
        create.update(REVIEW)
                .set(REVIEW.RATING, entity.rating())
                .set(REVIEW.REVIEW_BODY, JSONB.valueOf(entity.body()))
                .set(REVIEW.MODIFIED_TIME, entity.modifiedTime())
                .where(REVIEW.REVIEW_ID.eq(entity.reviewId()))
                .execute();
    }

    @Override
    public void deleteReview(UUID reviewId) {
        create.deleteFrom(REVIEW)
                .where(REVIEW.REVIEW_ID.eq(reviewId))
                .execute();
    }
}
