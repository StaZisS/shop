package com.example.shop.core.review.repository;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewEntity(
        UUID reviewId,
        UUID clientId,
        String productCode,
        OffsetDateTime createdTime,
        OffsetDateTime modifiedTime,
        int rating,
        String body
) {
}
