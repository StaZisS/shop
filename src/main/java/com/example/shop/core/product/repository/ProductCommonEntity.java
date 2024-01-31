package com.example.shop.core.product.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductCommonEntity (
        String code,
        UUID storeId,
        List<String> imagesUrl,
        String name,
        String normalizeName,
        BigDecimal price,
        double rating,
        int orderQuantity,
        String additionalInfo
){
}
