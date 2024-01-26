package com.example.shop.client;

import com.example.shop.ShopApplication;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.repository.ClientRepositoryImpl;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@Configuration
@ImportAutoConfiguration({
        ShopApplication.class
})
public class ClientIntegrationTestConfiguration {
}
