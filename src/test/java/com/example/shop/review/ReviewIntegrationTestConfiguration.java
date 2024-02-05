package com.example.shop.review;

import com.example.shop.core.order.repository.OrderRepositoryImpl;
import com.example.shop.core.review.repository.ReviewRepositoryImpl;
import com.example.shop.core.review.service.ReviewService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Configuration;

@Configuration
@JooqTest
@ImportAutoConfiguration({
        ReviewService.class,
        ReviewRepositoryImpl.class,
        OrderRepositoryImpl.class
})
public class ReviewIntegrationTestConfiguration {
}
