package com.example.shop.public_interface.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderSummaryDto(
        UUID orderId,
        String addressDeliveryCode,
        String addressDelivery,
        BigDecimal totalPrice,
        String status
) {
}
