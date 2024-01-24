package com.example.shop.core.product.repository;

import com.example.shop.public_.tables.records.ProductRecord;
import org.jooq.RecordMapper;

import java.util.Collections;

public class ProductCommonMapper implements RecordMapper<ProductRecord, ProductCommonEntity> {
    @Override
    public ProductCommonEntity map(ProductRecord productRecord) {
        return new ProductCommonEntity(
                productRecord.getCode(),
                Collections.emptyList(),
                productRecord.getName(),
                productRecord.getNormalizedName(),
                productRecord.getPrice(),
                productRecord.getRating(),
                productRecord.getOrderQuantity(),
                productRecord.getAdditionalInfo().data()
        );
    }
}
