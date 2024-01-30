package com.example.shop.core.store.repository;

import com.example.shop.public_interface.store.StoreCreateDto;

public interface StoreRepository {
    StoreEntity createIfNotExist(StoreCreateDto dto);
    void addStoreEmployee(EmployeeEntity entity);
}
