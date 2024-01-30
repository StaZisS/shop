package com.example.shop.core.store.repository;

import java.util.UUID;

public record StoreEntity(
        UUID storeId,
        String storeName
) {
}
