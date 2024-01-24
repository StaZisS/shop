package com.example.shop.publicInterface.mapper;

import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.publicInterface.ProductCommonDto;
import org.jooq.RecordMapper;

public class CommonProductMapEntityToDto {
    public static ProductCommonDto map(ProductCommonEntity productCommonEntity) {
        return new ProductCommonDto(
                productCommonEntity.imagesUrl(),
                productCommonEntity.name(),
                productCommonEntity.price(),
                productCommonEntity.rating(),
                productCommonEntity.orderQuantity()
        );
    }
}
