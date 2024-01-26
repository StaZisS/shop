package com.example.shop.product;

import com.example.shop.ShopApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({
        ShopApplication.class
})
public class ProductIntegrationTestConfiguration {
}
