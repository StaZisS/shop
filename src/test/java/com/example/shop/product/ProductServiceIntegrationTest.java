package com.example.shop.product;

import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.core.product.service.ProductService;
import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.ProductCommonDto;
import com.example.shop.public_interface.product.SortType;
import com.example.shop.public_interface.product.mapper.CommonProductMapEntityToDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {ProductIntegrationTestConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceIntegrationTest {
    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("Shop")
            .withUsername("postgres")
            .withPassword("veryStrongPassword");

    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.username", database::getUsername);
    }

    @Test
    @Order(1)
    public void setup() {
        var listProducts = getProductList();

        for (ProductCommonEntity listProduct : listProducts) {
            productRepository.addProduct(listProduct);
        }
    }

    @Test
    public void getProducts() {
        var filterDto = FilterDto.getDefault();
        var products = productService.getProducts(filterDto);

        assertFalse(products.products().isEmpty());
    }

    @Test
    public void getProductsSortedByPriceAsc() {
        var productList = mapListEntityToDto(getProductList());
        var filterDto = getFilterDtoWithSortType(SortType.PRICE_ASC);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonDto::price);

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.products().toArray()
        );
    }

    @Test
    public void getProductsSortedByPriceDesc() {
        var productList = mapListEntityToDto(getProductList());
        var filterDto = getFilterDtoWithSortType(SortType.PRICE_DESC);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonDto::price)
                .reversed();

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.products().toArray()
        );
    }

    @Test
    public void getProductsSortedByOrderDesc() {
        var productList = mapListEntityToDto(getProductList());
        var filterDto = getFilterDtoWithSortType(SortType.TOTAL_ORDER_DESC);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonDto::orderQuantity)
                .reversed();

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.products().toArray()
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
                        "{}"
                ),
                new ProductCommonEntity(
                        "2",
                        Collections.emptyList(),
                        "Мышь",
                        "мышь",
                        BigDecimal.valueOf(543.21),
                        3.4,
                        60,
                        "{}"
                )
        );
    }

    private List<ProductCommonDto> getSortedProducts(List<ProductCommonDto> products, Comparator<ProductCommonDto> comparator) {
        var sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(comparator);
        return sortedProducts;
    }

    private List<ProductCommonDto> mapListEntityToDto(List<ProductCommonEntity> entityList) {
        return entityList.stream()
                .map(CommonProductMapEntityToDto::map)
                .collect(Collectors.toList());
    }

    private FilterDto getFilterDtoWithSortType(SortType type) {
        return new FilterDto(
                "",
                type,
                new FilterDto.PaginationProperty(getProductList().size(), 1)
        );
    }
}
