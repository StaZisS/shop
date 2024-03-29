package com.example.shop.core.product.repository;

import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.ProductPageDto;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    ProductPageDto getCommonProductsPage(FilterDto filterDto);
    Optional<ProductCommonEntity> addProduct(ProductCommonEntity entity);
    int getCountProducts(String query);
    Optional<ProductCommonEntity> getCommonProduct(String productCode);
    List<ProductCommonEntity> getCommonProducts(List<String> productsCode);
}
