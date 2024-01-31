package com.example.shop.client;

import com.example.shop.ShopApplication;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.repository.ClientRepositoryImpl;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.core.client.validation.ClientValidationService;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@Configuration
@ImportAutoConfiguration({
        ClientService.class,
        ClientRepositoryImpl.class,
        ClientValidationService.class
})
@JooqTest
public class ClientIntegrationTestConfiguration {
}
