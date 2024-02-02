package com.example.shop.core.cart;

import java.util.List;
import java.util.UUID;

public interface CartRepository {
    int getCountProduct(UUID clientId, String productCode);
    void setCountProduct(CartEntity entity);
    void deleteProductPosition(UUID clientId, String productCode);
    List<CartEntity> getListCartEntity(List<String> productsCode, UUID clientId);
    void deletePositions(List<String> productsCode, UUID clientId);
}
