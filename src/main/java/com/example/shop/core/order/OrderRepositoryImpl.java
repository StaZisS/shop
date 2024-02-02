package com.example.shop.core.order;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final DSLContext create;

    @Override
    public void createOrder(OrderEntity entity, List<ProductInOrderEntity> productsInOrder) {

    }
}
