package com.example.shop.product;

import com.example.shop.core.product.repository.ProductRepositoryImpl;
import com.example.shop.core.product.service.ProductService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ImportAutoConfiguration({
        ProductService.class,
        ProductRepositoryImpl.class
})
@JooqTest
public class ProductIntegrationTestConfiguration {
}
