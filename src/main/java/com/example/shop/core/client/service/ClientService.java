package com.example.shop.core.client.service;

import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.validation.ClientValidationService;
import com.example.shop.core.util.PasswordTool;
import com.example.shop.public_interface.client.CreateClientDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientValidationService clientValidationService;

    public Optional<ClientEntity> createClient(CreateClientDto dto) {
        clientValidationService.validateCreateClient(dto);

        var entity = toFormEntityFromDto(dto);

        return clientRepository.createClient(entity);
    }

    public Optional<ClientEntity> getByEmail(String email) {
        return clientRepository.getClientByEmail(email);
    }

    public Optional<ClientEntity> getByClientId(String clientId) {
        try {
            final UUID clientIdUuid = UUID.fromString(clientId);
            return clientRepository.getClientByClientId(clientIdUuid);
        } catch (IllegalArgumentException e) {
            throw new ExceptionInApplication("Не удалось распарсить clientId", ExceptionType.ILLEGAL);
        }
    }

    private ClientEntity toFormEntityFromDto(CreateClientDto dto) {
        return new ClientEntity(
                UUID.randomUUID(),
                dto.name(),
                dto.email(),
                PasswordTool.getHashPassword(dto.password()),
                dto.birthDate(),
                dto.gender(),
                OffsetDateTime.now()
        );
    }
}
