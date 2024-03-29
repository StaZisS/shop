package com.example.shop.favorites;

import com.example.shop.core.favorites.repository.FavoriteRepositoryImpl;
import com.example.shop.core.favorites.service.FavoriteService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Configuration;

@Configuration
@JooqTest
@ImportAutoConfiguration({
        FavoriteService.class,
        FavoriteRepositoryImpl.class
})
public class FavoriteIntegrationTestConfiguration {
}
