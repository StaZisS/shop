package com.example.shop.core.order.repository;

import com.example.shop.public_.tables.records.ProductInOrderRecord;
import org.jooq.RecordMapper;

public class ProductInOrderEntityMapper implements RecordMapper<ProductInOrderRecord, ProductInOrderEntity> {
    @Override
    public ProductInOrderEntity map(ProductInOrderRecord productInOrderRecord) {
        return new ProductInOrderEntity(
                productInOrderRecord.getOrderId(),
                productInOrderRecord.getProductCode(),
                productInOrderRecord.getCountProduct()
        );
    }
}
