package com.example.shop.public_interface.review;

import java.util.UUID;

public record ReviewUpdateDto(
        UUID reviewId,
        UUID clientId,
        int rating,
        String reviewBody
) {
}
