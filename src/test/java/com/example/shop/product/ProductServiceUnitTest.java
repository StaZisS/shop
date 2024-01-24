package com.example.shop.product;


import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.core.product.repository.ProductRepositoryImpl;
import com.example.shop.core.product.service.ProductService;
import com.example.shop.publicInterface.FilterDto;
import com.example.shop.publicInterface.ProductCommonDto;
import com.example.shop.publicInterface.SortType;
import com.example.shop.publicInterface.mapper.CommonProductMapEntityToDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
        var productList = getProductList();
        var filterDto = FilterDto.getDefault();

        when(productRepository.getCommonProducts(filterDto))
                .thenReturn(productList);

        var receivedProductList = productService.getProducts(filterDto);

        assertArrayEquals(mapListEntityToDto(productList).toArray(), receivedProductList.toArray());
    }

    @Test
    public void getProductsSortedByPriceAsc() {
        var productList = getProductList();
        var filterDto = new FilterDto(
                "",
                SortType.PRICE_ASC
        );

        when(productRepository.getCommonProducts(filterDto))
                .thenReturn(productList);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonEntity::price);

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.toArray()
        );
    }

    @Test
    public void getProductsSortedByPriceDesc() {
        var productList = getProductList();
        var filterDto = new FilterDto(
                "",
                SortType.PRICE_DESC
        );

        when(productRepository.getCommonProducts(filterDto))
                .thenReturn(productList);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonEntity::price)
                .reversed();

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.toArray()
        );
    }

    @Test
    public void getProductsSortedByOrderDesc() {
        var productList = getProductList();
        var filterDto = new FilterDto(
                "",
                SortType.TOTAL_ORDER_DESC
        );

        when(productRepository.getCommonProducts(filterDto))
                .thenReturn(productList);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonEntity::orderQuantity)
                .reversed();

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.toArray()
        );
    }

    private List<ProductCommonEntity> getProductList() {
        return List.of(
                new ProductCommonEntity(
                        "1",
                        Collections.emptyList(),
                        "Клавиатура",
                        "клавиатура",
                        BigDecimal.valueOf(123.45),
                        4.6,
                        34,
                        ""
                ),
                new ProductCommonEntity(
                        "2",
                        Collections.emptyList(),
                        "Мышь",
                        "мышь",
                        BigDecimal.valueOf(543.21),
                        3.4,
                        60,
                        ""
                )
        );
    }

    private List<ProductCommonDto> getSortedProducts(List<ProductCommonEntity> products, Comparator<ProductCommonEntity> comparator) {
        var sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(comparator);
        return mapListEntityToDto(sortedProducts);
    }

    private List<ProductCommonDto> mapListEntityToDto(List<ProductCommonEntity> entityList) {
        return entityList.stream()
                .map(CommonProductMapEntityToDto::map)
                .collect(Collectors.toList());
    }
}
