package com.example.shop.core.client.repository;

import com.example.shop.public_interface.client.CreateClientDto;

import java.util.Optional;

public interface ClientRepository {
    void createClient(ClientEntity entity);
    Optional<ClientEntity> getClientByEmail(String email);
}
