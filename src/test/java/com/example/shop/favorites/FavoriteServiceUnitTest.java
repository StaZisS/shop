package com.example.shop.favorites;

import com.example.shop.core.favorites.FavoriteRepository;
import com.example.shop.core.favorites.FavoriteRepositoryImpl;
import com.example.shop.core.favorites.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class FavoriteServiceUnitTest {
    private FavoriteService favoriteService;
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    public void setup() {
        favoriteRepository = mock(FavoriteRepositoryImpl.class);

        favoriteService = new FavoriteService(favoriteRepository);
    }

    @Test
    public void test() {

    }
}
