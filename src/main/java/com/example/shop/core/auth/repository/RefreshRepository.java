package com.example.shop.core.auth.repository;

import com.example.shop.core.auth.entity.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshRepository {
    void saveRefreshToken(RefreshTokenEntity entity);
    Optional<RefreshTokenEntity> getRefreshTokenByClientId(String clientId);
    void deleteRefreshToken(String clientId);
}
