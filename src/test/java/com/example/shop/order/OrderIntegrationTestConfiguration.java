package com.example.shop.order;

import com.example.shop.core.cart.repository.CartRepositoryImpl;
import com.example.shop.core.client.repository.ClientRepositoryImpl;
import com.example.shop.core.order.repository.OrderRepositoryImpl;
import com.example.shop.core.order.service.OrderService;
import com.example.shop.core.product.repository.ProductRepositoryImpl;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Configuration;

@Configuration
@JooqTest
@ImportAutoConfiguration({
        OrderService.class,
        OrderRepositoryImpl.class,
        CartRepositoryImpl.class,
        ProductRepositoryImpl.class,
        ClientRepositoryImpl.class
})
public class OrderIntegrationTestConfiguration {
}
