package com.example.shop.product;

import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.core.product.repository.ProductRepositoryImpl;
import com.example.shop.core.product.service.ProductService;
import com.example.shop.publicInterface.FilterDto;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.BeforeAll;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {IntegrationTestConfiguration.class})
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
        var listProducts = List.of(
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

        for (ProductCommonEntity listProduct : listProducts) {
            productRepository.addProduct(listProduct);
        }
    }

    @Test
    public void getProducts() {
        var filterDto = FilterDto.getDefault();
        var products = productService.getProducts(filterDto);

        assertFalse(products.isEmpty());
    }
}
