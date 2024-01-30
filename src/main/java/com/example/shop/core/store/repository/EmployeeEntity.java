package com.example.shop.core.store.repository;

import com.example.shop.public_interface.store.EmployeePosition;

import java.util.Set;
import java.util.UUID;

public record EmployeeEntity(
        UUID clientId,
        UUID storeId,
        Set<EmployeePosition> positions
) {
}
