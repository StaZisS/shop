package com.example.shop.client;

import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.public_interface.client.ClientCreateDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {ClientIntegrationTestConfiguration.class})
public class ClientServiceIntegrationTest {
    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("Shop")
            .withUsername("postgres")
            .withPassword("veryStrongPassword");

    @Autowired private ClientService clientService;
    @Autowired private ClientRepository clientRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.username", database::getUsername);
    }

    @Test
    public void createClient() {
        var createClientDto = formatClientCreateDto("ggwp@gmail.com");

        clientService.createClient(createClientDto);

        var clientEntityOptional = clientRepository.getClientByEmail(createClientDto.email());

        if (clientEntityOptional.isEmpty()) {
            fail("Пользователь не найден");
        }

        assertEquals(createClientDto.email(), clientEntityOptional.get().email());
    }

    @Test
    public void createClientWithDuplicatedEmail() {
        var createClientDto = formatClientCreateDto("easy@gmail.com");

        clientService.createClient(createClientDto);

        var thrownException = assertThrows(ExceptionInApplication.class, () -> {
            clientService.createClient(createClientDto);
        });

        assertEquals(ExceptionType.ALREADY_EXISTS ,thrownException.getType());
    }

    @Test
    public void getClientProfile() {
        var createClientDto = formatClientCreateDto("easy1@gmail.com");

        var clientId = clientService.createClient(createClientDto);

        var profileDto = clientService.getProfile(clientId);

        assertEquals(createClientDto.email(), profileDto.email());
    }

    @Test
    public void getNotExistClientProfile() {
        var randomId = UUID.randomUUID();
        assertThrows(ExceptionInApplication.class, () -> clientService.getProfile(randomId));
    }

    private ClientCreateDto formatClientCreateDto(String email) {
        return new ClientCreateDto(
                "Sasha",
                email,
                "veryStrongPassword",
                OffsetDateTime.now(),
                "UNSPECIFIED"
        );
    }
}
