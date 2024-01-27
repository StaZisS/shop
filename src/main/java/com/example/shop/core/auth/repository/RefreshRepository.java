package com.example.shop.core.auth.repository;

import java.util.Optional;

public interface RefreshRepository {
    //TODO:нужно ещё сохранять дату истечения токена
    Optional<String> saveRefreshToken(String clientId, String refreshToken);
    Optional<String> getRefreshTokenByClientId(String clientId);
    void deleteRefreshToken(String refreshToken);
}
