package com.example.shop.core.product.repository;

import com.example.shop.public_interface.ProductCommonDto;
import com.example.shop.public_.tables.records.GetProductsPagedRecord;
import org.jooq.RecordMapper;

import java.util.Collections;

public class ProductCommonMapper implements RecordMapper<GetProductsPagedRecord, ProductCommonDto> {
    @Override
    public ProductCommonDto map(GetProductsPagedRecord productRecord) {
        return new ProductCommonDto(
                productRecord.getCode(),
                Collections.emptyList(),
                productRecord.getName(),
                productRecord.getPrice(),
                productRecord.getRating(),
                productRecord.getOrderQuantity()
        );
    }
}
