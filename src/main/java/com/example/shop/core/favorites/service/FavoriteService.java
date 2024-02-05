package com.example.shop.core.favorites.service;

import com.example.shop.core.favorites.repository.FavoriteRepository;
import com.example.shop.public_interface.favorites.FavoriteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public void switchFavorite(FavoriteDto dto) {
        favoriteRepository.switchFavoriteStatus(dto.clientId(), dto.productCode());
    }
}
