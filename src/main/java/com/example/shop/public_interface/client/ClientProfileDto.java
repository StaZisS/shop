package com.example.shop.public_interface.client;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClientProfileDto(
    UUID clientId,
    String name,
    String email,
    OffsetDateTime birthDate,
    String gender
) {
}
