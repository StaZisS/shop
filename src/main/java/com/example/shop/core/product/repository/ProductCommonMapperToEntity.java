package com.example.shop.core.product.repository;

import com.example.shop.public_.tables.records.ProductRecord;
import org.jooq.Record1;
import org.jooq.RecordMapper;

import java.util.Collections;

public class ProductCommonMapperToEntity implements RecordMapper<Record1<ProductRecord>, ProductCommonEntity> {

    @Override
    public ProductCommonEntity map(Record1<ProductRecord> productRecordRecord1) {
        return new ProductCommonEntity(
                productRecordRecord1.value1().getCode(),
                productRecordRecord1.value1().getStoreId(),
                Collections.emptyList(),
                productRecordRecord1.value1().getName(),
                productRecordRecord1.value1().getNormalizedName(),
                productRecordRecord1.value1().getPrice(),
                productRecordRecord1.value1().getRating(),
                productRecordRecord1.value1().getOrderQuantity(),
                productRecordRecord1.value1().getAdditionalInfo().data()
        );
    }
}
