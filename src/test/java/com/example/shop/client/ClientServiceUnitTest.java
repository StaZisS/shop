package com.example.shop.client;

import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.repository.ClientRepositoryImpl;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.core.client.service.PasswordService;
import com.example.shop.public_interface.client.CreateClientDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientServiceUnitTest {
    private ClientService clientService;
    private ClientRepository clientRepository;
    private PasswordService passwordService;

    @BeforeEach
    public void setup() {
        clientRepository = mock(ClientRepositoryImpl.class);
        passwordService = mock(PasswordService.class);

        clientService = new ClientService(clientRepository, passwordService);
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

        when(passwordService.getHashPassword(createClientDto.password()))
                .thenReturn(createClientDto.password());

        clientService.createClient(createClientDto);
    }

    @Test
    public void createClientWithDuplicatedEmail() {
        var createClientDto = new CreateClientDto(
                "Sasha",
                "ggwp@gmail.com",
                "veryStrongPassword",
                OffsetDateTime.now(),
                "UNSPECIFIED"
        );

        when(passwordService.getHashPassword(createClientDto.password()))
                .thenReturn(createClientDto.password());

        doThrow(new ExceptionInApplication("Клиент с такой почтой уже существует", ExceptionType.ALREADY_EXISTS))
                .when(clientRepository)
                .createClient(any());

        var thrownException = assertThrows(ExceptionInApplication.class, () -> {
            clientService.createClient(createClientDto);
        });

        assertEquals(ExceptionType.ALREADY_EXISTS ,thrownException.getType());
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

        when(passwordService.getHashPassword(createClientDto.password()))
                .thenReturn(createClientDto.password());

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

        when(passwordService.getHashPassword(createClientDto.password()))
                .thenReturn(createClientDto.password());

        var thrownException = assertThrows(ExceptionInApplication.class, () -> {
            clientService.createClient(createClientDto);
        });

        assertEquals(ExceptionType.INVALID ,thrownException.getType());
    }
}
