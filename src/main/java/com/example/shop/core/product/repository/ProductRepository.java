package com.example.shop.core.product.repository;

import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.ProductPageDto;

import java.util.Optional;

public interface ProductRepository {
    ProductPageDto getCommonProductsPage(FilterDto filterDto);
    Optional<ProductCommonEntity> addProduct(ProductCommonEntity entity);
    int getCountProducts(String query);
}
