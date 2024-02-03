package com.example.shop.core.order.repository;

import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;

public enum DeliveryStatus {
    PROCESSING,
    APPLY,
    CANCELED,
    IN_DELIVER,
    DELIVERED,
    ;

    public static DeliveryStatus getDeliveryStatusByName(String name) {
        try {
            return DeliveryStatus.valueOf(name);
        } catch (Exception e) {
            throw new ExceptionInApplication("Данного статуса заказа не существует", ExceptionType.NOT_FOUND);
        }
    }
}
