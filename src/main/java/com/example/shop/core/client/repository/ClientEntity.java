package com.example.shop.core.client.repository;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClientEntity(
        UUID clientId,
        String name,
        String email,
        String password,
        OffsetDateTime birthDate,
        String gender,
        OffsetDateTime createdDate
) {
}
