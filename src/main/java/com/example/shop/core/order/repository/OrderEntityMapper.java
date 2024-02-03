package com.example.shop.core.order.repository;

import com.example.shop.public_.tables.records.OrdersRecord;
import org.jooq.RecordMapper;

public class OrderEntityMapper implements RecordMapper<OrdersRecord, OrderEntity> {
    @Override
    public OrderEntity map(OrdersRecord ordersRecord) {
        return new OrderEntity(
                ordersRecord.getOrderId(),
                ordersRecord.getClientId(),
                ordersRecord.getAddressDeliveryCode(),
                ordersRecord.getAddressDelivery(),
                ordersRecord.getTotalPrice(),
                DeliveryStatus.getDeliveryStatusByName(ordersRecord.getStatus()),
                ordersRecord.getCreationDate(),
                ordersRecord.getTrackNumber()
        );
    }
}
