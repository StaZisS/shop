package com.example.shop.core.auth.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshRepositoryImpl implements RefreshRepository {
    @Override
    public Optional<String> saveRefreshToken(String clientId, String refreshToken) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getRefreshTokenByClientId(String clientId) {
        return Optional.empty();
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {

    }
}
