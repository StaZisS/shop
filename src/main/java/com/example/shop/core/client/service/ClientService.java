package com.example.shop.core.client.service;

import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.validation.ClientCreateValidation;
import com.example.shop.public_interface.client.CreateClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordService passwordService;

    public void createClient(CreateClientDto dto) {
        ClientCreateValidation.validate(dto);

        var entity = toFormEntityFromDto(dto);

        clientRepository.createClient(entity);
    }

    private ClientEntity toFormEntityFromDto(CreateClientDto dto) {
        return new ClientEntity(
                UUID.randomUUID(),
                dto.name(),
                dto.email(),
                passwordService.getHashPassword(dto.password()),
                dto.birthDate(),
                dto.gender(),
                OffsetDateTime.now()
        );
    }
}
