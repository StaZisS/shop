package com.example.shop.public_interface.client;

import java.time.OffsetDateTime;

public record CreateClientDto(
        String name,
        String email,
        String password,
        OffsetDateTime birthDate,
        String gender
) {
}
