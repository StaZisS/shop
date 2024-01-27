package com.example.shop.core.auth.service;

import com.example.shop.core.auth.entity.RefreshTokenEntity;
import com.example.shop.core.auth.provider.DataForGenerateToken;
import com.example.shop.core.auth.provider.JwtProvider;
import com.example.shop.core.auth.repository.RefreshRepository;
import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.core.util.PasswordTool;
import com.example.shop.public_interface.auth.JwtResponseDto;
import com.example.shop.public_interface.auth.LoginDto;
import com.example.shop.public_interface.client.CreateClientDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//TODO: порефакторить убрать повторы, после напискания тестов
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final ClientService clientService;
    private final RefreshRepository repository;

    public JwtResponseDto login(@NonNull LoginDto dto) {
        final ClientEntity client = clientService.getByEmail(dto.email())
                .orElseThrow(() -> new ExceptionInApplication("Клиент не найден", ExceptionType.NOT_FOUND));

        var dataForGenerateToken = forGenerateToken(client);

        if (PasswordTool.isCorrectPassword(client.password(), dto.password())) {
            final String accessToken = jwtProvider.generateAccessToken(dataForGenerateToken);
            final String refreshToken = jwtProvider.generateRefreshToken(dataForGenerateToken);

            final RefreshTokenEntity refreshTokenEntity = getRefreshTokenEntity(refreshToken, client.clientId().toString());
            repository.saveRefreshToken(refreshTokenEntity);

            return new JwtResponseDto(accessToken, refreshToken);
        }

        throw new ExceptionInApplication("Неверная почта или пароль", ExceptionType.UNAUTHORIZED);
    }

    public JwtResponseDto register(@NonNull CreateClientDto dto) {
        clientService.createClient(dto)
                .orElseThrow(() -> new ExceptionInApplication("Клиент уже существует", ExceptionType.ALREADY_EXISTS));

        var loginDto = new LoginDto(dto.email(), dto.password());

        return login(loginDto);
    }

    public JwtResponseDto getAccessToken(@NonNull String refreshToken) {
        jwtProvider.validateRefreshToken(refreshToken);

        final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        final String clientId = claims.getSubject();

        final RefreshTokenEntity oldRefreshToken = repository.getRefreshTokenByClientId(clientId)
                .orElseThrow(() -> new ExceptionInApplication("RefreshToken истек", ExceptionType.UNAUTHORIZED));
        if (!oldRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new ExceptionInApplication("Недействительный RefreshToken", ExceptionType.UNAUTHORIZED);
        }

        final ClientEntity client = clientService.getByClientId(clientId)
                .orElseThrow(() -> new ExceptionInApplication("Клиент не найден", ExceptionType.NOT_FOUND));
        var dataForGenerateToken = forGenerateToken(client);

        final String accessToken = jwtProvider.generateAccessToken(dataForGenerateToken);
        return new JwtResponseDto(accessToken, null);
    }

    public JwtResponseDto refresh(@NonNull String refreshToken) {
        jwtProvider.validateRefreshToken(refreshToken);

        final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        final String clientId = claims.getSubject();

        final RefreshTokenEntity oldRefreshToken = repository.getRefreshTokenByClientId(clientId)
                .orElseThrow(() -> new ExceptionInApplication("RefreshToken истек", ExceptionType.UNAUTHORIZED));
        if (!oldRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new ExceptionInApplication("Недействительный RefreshToken", ExceptionType.UNAUTHORIZED);
        }

        repository.deleteRefreshToken(oldRefreshToken.getClientId());

        final ClientEntity client = clientService.getByClientId(clientId)
                .orElseThrow(() -> new ExceptionInApplication("Клиент не найден", ExceptionType.NOT_FOUND));
        var dataForGenerateToken = forGenerateToken(client);

        final String accessToken = jwtProvider.generateAccessToken(dataForGenerateToken);
        final String newRefreshToken = jwtProvider.generateRefreshToken(dataForGenerateToken);

        final RefreshTokenEntity refreshTokenEntity = getRefreshTokenEntity(newRefreshToken, clientId);
        repository.saveRefreshToken(refreshTokenEntity);

        return new JwtResponseDto(accessToken, newRefreshToken);
    }

    private DataForGenerateToken forGenerateToken(ClientEntity entity) {
        return new DataForGenerateToken(entity.clientId().toString());
    }

    private RefreshTokenEntity getRefreshTokenEntity(String refreshToken, String clientId) {
        return new RefreshTokenEntity(
                clientId,
                refreshToken,
                jwtProvider.getRefreshTokenTtl()
        );
    }

}
