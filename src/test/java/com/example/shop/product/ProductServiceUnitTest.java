package com.example.shop.product;


import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.core.product.repository.ProductRepositoryImpl;
import com.example.shop.core.product.service.ProductService;
import com.example.shop.public_interface.FilterDto;
import com.example.shop.public_interface.PaginationDto;
import com.example.shop.public_interface.ProductCommonDto;
import com.example.shop.public_interface.ProductPageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductServiceUnitTest {
    private ProductService productService;
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        productRepository = mock(ProductRepositoryImpl.class);

        productService = new ProductService(productRepository);
    }

    @Test
    public void getProducts() {
        var productPage = getProductPage();
        var filterDto = FilterDto.getDefault();

        when(productRepository.getCommonProductsPage(filterDto))
                .thenReturn(productPage);
        when(productRepository.getCountProducts(any()))
                .thenReturn(2);

        var receivedProductList = productService.getProducts(filterDto);

        assertArrayEquals(
                productPage.products().toArray(),
                receivedProductList.products().toArray()
        );
    }

    private ProductPageDto getProductPage() {
        var pagination = new PaginationDto(2,1,1);
        return new ProductPageDto(
                getProductList(),
                pagination
        );
    }

    private List<ProductCommonDto> getProductList() {
        return List.of(
                new ProductCommonDto(
                        "1",
                        Collections.emptyList(),
                        "Клавиатура",
                        BigDecimal.valueOf(123.45),
                        4.6,
                        34
                ),
                new ProductCommonDto(
                        "2",
                        Collections.emptyList(),
                        "Мышь",
                        BigDecimal.valueOf(543.21),
                        3.4,
                        60
                )
        );
    }
}
