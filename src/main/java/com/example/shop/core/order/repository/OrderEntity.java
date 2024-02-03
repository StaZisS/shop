package com.example.shop.core.order.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderEntity(
        UUID orderId,
        UUID clientId,
        String addressDeliveryCode,
        String addressDelivery,
        BigDecimal totalPrice,
        DeliveryStatus status,
        OffsetDateTime creationDate,
        String trackNumber
) {
}
