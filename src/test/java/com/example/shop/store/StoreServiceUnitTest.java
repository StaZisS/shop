package com.example.shop.store;

import com.example.shop.core.store.StoreService;
import com.example.shop.core.store.repository.StoreRepository;
import com.example.shop.core.store.repository.StoreRepositoryImpl;
import com.example.shop.public_interface.store.StoreCreateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;

public class StoreServiceUnitTest {
    private StoreService storeService;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setup() {
        storeRepository = mock(StoreRepositoryImpl.class);

        storeService = new StoreService(storeRepository);
    }

    @Test
    public void createStore() {
        var storeCreateDto = new StoreCreateDto(
                "Рога и копыта",
                UUID.randomUUID()
        );

        storeService.createStore(storeCreateDto);
    }
}
