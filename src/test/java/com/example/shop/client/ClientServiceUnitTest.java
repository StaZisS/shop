package com.example.shop.client;

import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.repository.ClientRepositoryImpl;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.core.client.validation.ClientValidationService;
import com.example.shop.public_interface.client.CreateClientDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientServiceUnitTest {
    private ClientService clientService;
    private ClientRepository clientRepository;
    private ClientValidationService clientValidationService;

    @BeforeEach
    public void setup() {
        clientRepository = mock(ClientRepositoryImpl.class);

        clientValidationService = new ClientValidationService(clientRepository);
        clientService = new ClientService(clientRepository, clientValidationService);
    }

    @Test
    public void createClient() {
        var createClientDto = new CreateClientDto(
                "Sasha",
                "ggwp@gmail.com",
                "veryStrongPassword",
                OffsetDateTime.now(),
                "UNSPECIFIED"
        );

        clientService.createClient(createClientDto);
    }

    @Test
    public void createClientWithInvalidEmail() {
        var createClientDto = new CreateClientDto(
                "Sasha",
                "@gmail.com",
                "veryStrongPassword",
                OffsetDateTime.now(),
                "UNSPECIFIED"
        );

        when(clientRepository.getClientByEmail(createClientDto.email()))
                .thenReturn(Optional.of(mapCreateClientDtoToEntity(createClientDto)));

        var thrownException = assertThrows(ExceptionInApplication.class, () -> {
            clientService.createClient(createClientDto);
        });

        assertEquals(ExceptionType.INVALID ,thrownException.getType());
    }

    @Test
    public void createClientWithInvalidGender() {
        var createClientDto = new CreateClientDto(
                "Sasha",
                "ggwp@gmail.com",
                "veryStrongPassword",
                OffsetDateTime.now(),
                "MONSTER"
        );

        when(clientRepository.getClientByEmail(createClientDto.email()))
                .thenReturn(Optional.of(mapCreateClientDtoToEntity(createClientDto)));

        var thrownException = assertThrows(ExceptionInApplication.class, () -> {
            clientService.createClient(createClientDto);
        });

        assertEquals(ExceptionType.INVALID ,thrownException.getType());
    }

    private ClientEntity mapCreateClientDtoToEntity(CreateClientDto dto) {
        return new ClientEntity(
                UUID.randomUUID(),
                dto.name(),
                dto.email(),
                dto.password(),
                dto.birthDate(),
                dto.gender(),
                OffsetDateTime.now()
        );
    }
}
