package com.example.shop.core.order;

import java.util.UUID;

public record ProductInOrderEntity(
        UUID orderId,
        String productCode,
        int countProduct
) {
}
