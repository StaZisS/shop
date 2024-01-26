package com.example.shop.public_interface.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductCommonDto(
        String code,
        List<String> imagesUrl,
        String name,
        BigDecimal price,
        double rating,
        int orderQuantity
) {
}
