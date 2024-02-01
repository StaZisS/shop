package com.example.shop.core.favorites;

import java.util.UUID;

public interface FavoriteRepository {
    void switchFavoriteStatus(UUID clientId, String productCode);
    boolean isFavorite(UUID clientId, String productCode);
}
