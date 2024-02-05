package com.example.shop.core.review.repository;

import com.example.shop.public_.tables.records.ReviewRecord;
import org.jooq.RecordMapper;

public class ReviewEntityMapper implements RecordMapper<ReviewRecord, ReviewEntity> {
    @Override
    public ReviewEntity map(ReviewRecord record) {
        return new ReviewEntity(
                record.getReviewId(),
                record.getClientId(),
                record.getProductId(),
                record.getCreatedTime(),
                record.getModifiedTime(),
                record.getRating(),
                record.getReviewBody().data()
        );
    }
}
