package com.example.shop.public_interface.cart;

import java.util.UUID;

public record CartDto(
        UUID clientId,
        String productCode,
        int count
) {
}
