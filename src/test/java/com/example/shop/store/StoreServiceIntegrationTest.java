package com.example.shop.store;

import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.store.StoreService;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.store.StoreCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {StoreIntegrationTestConfiguration.class})
public class StoreServiceIntegrationTest {
    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("Shop")
            .withUsername("postgres")
            .withPassword("veryStrongPassword");

    @Autowired private StoreService storeService;
    @Autowired private ClientRepository clientRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.username", database::getUsername);
    }

    @Test
    public void createStore() {
        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                "name",
                "ggwp@mail.ru",
                "12345678",
                OffsetDateTime.now(),
                "MALE",
                OffsetDateTime.now()
        );

        clientRepository.createClient(clientEntity);

        var storeCreateDto = new StoreCreateDto(
                "Рога и копыта",
                clientEntity.clientId()
        );

        storeService.createStore(storeCreateDto);
    }

    @Test
    public void createStoreWithDuplicateName() {
        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                "name",
                "ggwp1@mail.ru",
                "12345678",
                OffsetDateTime.now(),
                "MALE",
                OffsetDateTime.now()
        );

        clientRepository.createClient(clientEntity);

        var storeCreateDto = new StoreCreateDto(
                "Кот в сапогах",
                clientEntity.clientId()
        );

        storeService.createStore(storeCreateDto);

        assertThrows(ExceptionInApplication.class, () -> storeService.createStore(storeCreateDto));
    }
}
