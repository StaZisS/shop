package com.example.shop.public_interface.favorites;

import java.util.UUID;

public record FavoriteDto(
        UUID clientId,
        String productCode
) {
}
