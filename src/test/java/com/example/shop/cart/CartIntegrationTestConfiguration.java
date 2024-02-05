package com.example.shop.cart;

import com.example.shop.core.cart.repository.CartRepositoryImpl;
import com.example.shop.core.cart.service.CartService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Configuration;

@Configuration
@JooqTest
@ImportAutoConfiguration({
        CartService.class,
        CartRepositoryImpl.class
})
public class CartIntegrationTestConfiguration {
}
