package com.example.shop.order;

import com.example.shop.core.order.OrderRepositoryImpl;
import com.example.shop.core.order.OrderService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Configuration;

@Configuration
@JooqTest
@ImportAutoConfiguration({
        OrderService.class,
        OrderRepositoryImpl.class
})
public class OrderIntegrationTestConfiguration {
}
