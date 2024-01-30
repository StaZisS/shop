package com.example.shop.core.client.repository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Optional<ClientEntity> createClient(ClientEntity entity);
    Optional<ClientEntity> getClientByEmail(String email);
    Optional<ClientEntity> getClientByClientId(UUID clientId);
}
