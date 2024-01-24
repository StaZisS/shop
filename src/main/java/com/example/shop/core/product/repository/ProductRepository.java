package com.example.shop.core.product.repository;

import com.example.shop.publicInterface.FilterDto;
import com.example.shop.publicInterface.ProductCommonDto;

import java.util.List;

public interface ProductRepository {
    List<ProductCommonEntity> getCommonProducts(FilterDto filterDto);
    void addProduct(ProductCommonEntity entity);
}
