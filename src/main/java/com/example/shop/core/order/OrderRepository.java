package com.example.shop.core.order;

import java.util.List;

public interface OrderRepository {
    void createOrder(OrderEntity entity, List<ProductInOrderEntity> productsInOrder);
}
