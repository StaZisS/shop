package com.example.shop.public_interface.store;

import java.util.UUID;

public record StoreCreateDto(
        String storeName,
        UUID clientId
) {
}
