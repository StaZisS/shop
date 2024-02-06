package com.example.shop.public_interface.review;

import java.util.UUID;

public record ReviewDeleteDto(
        UUID reviewId,
        UUID clientId
) {
}
