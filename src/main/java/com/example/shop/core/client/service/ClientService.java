package com.example.shop.core.client.service;

import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.validation.ClientValidationService;
import com.example.shop.core.util.PasswordTool;
import com.example.shop.public_interface.client.ClientCreateDto;
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

    public Optional<ClientEntity> createClient(ClientCreateDto dto) {
        clientValidationService.validateCreateClient(dto);

        var entity = toFormEntityFromDto(dto);

        return clientRepository.createClient(entity);
    }

    public Optional<ClientEntity> getByEmail(String email) {
        return clientRepository.getClientByEmail(email);
    }

    public Optional<ClientEntity> getByClientId(UUID clientId) {
        return clientRepository.getClientByClientId(clientId);
    }

    private ClientEntity toFormEntityFromDto(ClientCreateDto dto) {
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
