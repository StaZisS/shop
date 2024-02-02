package com.example.shop.core.cart;

import com.example.shop.public_.tables.records.CartRecord;
import org.jooq.RecordMapper;

public class CartEntityMapper implements RecordMapper<CartRecord, CartEntity> {
    @Override
    public CartEntity map(CartRecord cartRecord) {
        return new CartEntity(
                cartRecord.getClientId(),
                cartRecord.getProductCode(),
                cartRecord.getCountProduct()
        );
    }
}
