package com.example.shop.core.order.repository;

import java.util.UUID;

public record ProductInOrderEntity(
        UUID orderId,
        String productCode,
        int countProduct
) {
}
