package com.example.shop.core.cart;

import java.util.UUID;

public interface CartRepository {
    int getCountProduct(UUID clientId, String productCode);
    void setCountProduct(CartEntity entity);
    void deleteProductPosition(UUID clientId, String productCode);
}
