package com.example.shop.core.product.repository;

import java.math.BigDecimal;
import java.util.List;

public record ProductCommonEntity (
        String code,
        List<String> imagesUrl,
        String name,
        String normalizeName,
        BigDecimal price,
        double rating,
        int orderQuantity,
        String additionalInfo
){
}
