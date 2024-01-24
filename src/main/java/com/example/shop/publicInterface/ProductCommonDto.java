package com.example.shop.publicInterface;

import java.math.BigDecimal;
import java.util.List;

public record ProductCommonDto(
    List<String> imagesUrl,
    String name,
    BigDecimal price,
    double rating,
    int orderQuantity
) {
}
