package com.example.shop.public_interface.order;

import java.math.BigDecimal;

public record ProductDetailsDto(
        String code,
        String name,
        BigDecimal price,
        int countInOrder
) {
}
