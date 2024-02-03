package com.example.shop.public_interface.order;

import com.example.shop.public_interface.product.ProductCommonDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailsDto(
        UUID orderId,
        String addressDeliveryCode,
        String addressDelivery,
        BigDecimal totalPrice,
        String status,
        OffsetDateTime creationDate,
        String trackNumber,
        List<ProductDetailsDto> products
) {
}
