package com.example.shop.public_interface.review;

import java.util.UUID;

public record ReviewCreateDto(
        UUID clientId,
        String productCode,
        int rating,
        String reviewBody
) {
}
