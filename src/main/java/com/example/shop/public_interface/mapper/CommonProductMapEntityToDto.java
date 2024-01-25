package com.example.shop.public_interface.mapper;

import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.public_interface.ProductCommonDto;

public class CommonProductMapEntityToDto {
    public static ProductCommonDto map(ProductCommonEntity productCommonEntity) {
        return new ProductCommonDto(
                productCommonEntity.code(),
                productCommonEntity.imagesUrl(),
                productCommonEntity.name(),
                productCommonEntity.price(),
                productCommonEntity.rating(),
                productCommonEntity.orderQuantity()
        );
    }
}
