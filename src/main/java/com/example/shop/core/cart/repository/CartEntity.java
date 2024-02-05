package com.example.shop.core.cart.repository;

import java.util.UUID;

public record CartEntity(
        UUID clientId,
        String productCode,
        int countProduct
) {
}
