package com.example.shop.store;

import com.example.shop.ShopApplication;
import com.example.shop.core.client.repository.ClientRepositoryImpl;
import com.example.shop.core.store.StoreService;
import com.example.shop.core.store.repository.StoreRepositoryImpl;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({
        StoreRepositoryImpl.class,
        StoreService.class,
        ClientRepositoryImpl.class
})
@JooqTest
public class StoreIntegrationTestConfiguration {

}
