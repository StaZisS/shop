package com.example.shop.core.product.repository;

import com.example.shop.public_interface.FilterDto;
import com.example.shop.public_interface.ProductPageDto;

public interface ProductRepository {
    ProductPageDto getCommonProductsPage(FilterDto filterDto);
    void addProduct(ProductCommonEntity entity);
    int getCountProducts(String query);
}
