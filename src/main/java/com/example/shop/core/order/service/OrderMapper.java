package com.example.shop.core.order.service;

import com.example.shop.core.order.repository.OrderEntity;
import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.public_interface.order.OrderDetailsDto;
import com.example.shop.public_interface.order.OrderSummaryDto;
import com.example.shop.public_interface.order.ProductDetailsDto;

import java.util.List;

public class OrderMapper {
    public static OrderDetailsDto mapEntityToDto(OrderEntity entity, List<ProductDetailsDto> products) {
        return new OrderDetailsDto(
                entity.orderId(),
                entity.addressDeliveryCode(),
                entity.addressDelivery(),
                entity.totalPrice(),
                entity.status().name(),
                entity.creationDate(),
                entity.trackNumber(),
                products
        );
    }

    public static ProductDetailsDto mapEntityToDto(ProductCommonEntity entity, int countInOrder) {
        return new ProductDetailsDto(
                entity.code(),
                entity.name(),
                entity.price(),
                countInOrder
        );
    }

    public static OrderSummaryDto mapEntityToDto(OrderEntity entity) {
        return new OrderSummaryDto(
                entity.orderId(),
                entity.addressDeliveryCode(),
                entity.addressDelivery(),
                entity.totalPrice(),
                entity.status().name()
        );
    }
}
