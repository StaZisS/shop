package com.example.shop.core.order.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void createOrder(OrderEntity entity, List<ProductInOrderEntity> productsInOrder);
    Optional<OrderEntity> getOrder(UUID orderId);
    List<ProductInOrderEntity> getProductsInOrder(UUID orderId);
    void cancelOrder(UUID orderId);
    List<OrderEntity> getOrders(UUID clientId);
    List<OrderEntity> getAllOrdersWithProduct(UUID clientId, String productCode);
}
