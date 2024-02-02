package com.example.shop.public_interface.order;

import java.util.List;
import java.util.UUID;

public record CreateOrderDto(
        UUID clientId,
        String addressDeliveryCode,
        List<String> productsCodeInCart
) {
}
