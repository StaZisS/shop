package com.example.shop.core.auth.service;

import com.example.shop.core.auth.entity.RefreshTokenEntity;
import com.example.shop.core.auth.provider.DataForGenerateToken;
import com.example.shop.core.auth.provider.JwtProvider;
import com.example.shop.core.auth.repository.RefreshRepository;
import com.example.shop.public_interface.auth.JwtResponseDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshRepository refreshRepository;
    private final JwtProvider jwtProvider;

    public JwtResponseDto getTokens(DataForGenerateToken data) {
        final String accessToken = jwtProvider.generateAccessToken(data);
        final String refreshToken = jwtProvider.generateRefreshToken(data);

        final RefreshTokenEntity refreshTokenEntity = getRefreshTokenEntity(refreshToken);
        refreshRepository.saveRefreshToken(refreshTokenEntity);

        return new JwtResponseDto(accessToken, refreshToken);
    }

    public void checkRefreshToken(String refreshToken) {
        jwtProvider.validateRefreshToken(refreshToken);

        final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        final String tokenId = claims.getId();

        final RefreshTokenEntity oldRefreshToken = refreshRepository.getRefreshTokenById(tokenId)
                .orElseThrow(() -> new ExceptionInApplication("RefreshToken истек", ExceptionType.UNAUTHORIZED));
        if (!oldRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new ExceptionInApplication("Недействительный RefreshToken", ExceptionType.UNAUTHORIZED);
        }
    }

    public String getClientIdInRefreshToken(String refreshToken) {
        return jwtProvider.getRefreshClaims(refreshToken).getSubject();
    }

    public String getTokenIdInRefreshToken(String refreshToken) {
        return jwtProvider.getRefreshClaims(refreshToken).getId();
    }

    public JwtResponseDto getAccessToken(DataForGenerateToken data) {
        var accessToken = jwtProvider.generateAccessToken(data);
        return new JwtResponseDto(accessToken, null);
    }

    public void deleteRefreshToken(String tokenId) {
        refreshRepository.deleteRefreshToken(tokenId);
    }

    private RefreshTokenEntity getRefreshTokenEntity(String refreshToken) {
        return new RefreshTokenEntity(
                jwtProvider.getRefreshClaims(refreshToken).getId(),
                refreshToken,
                jwtProvider.getRefreshTokenTtl()
        );
    }
}
