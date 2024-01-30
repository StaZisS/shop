package com.example.shop.core.store;

import com.example.shop.core.store.repository.StoreRepository;
import com.example.shop.public_interface.store.StoreCreateDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public void createStore(@NonNull StoreCreateDto dto) {
        storeRepository.createIfNotExist(dto);
    }
}
