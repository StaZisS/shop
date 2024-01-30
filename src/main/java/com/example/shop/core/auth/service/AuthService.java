package com.example.shop.core.auth.service;

import com.example.shop.core.auth.provider.DataForGenerateToken;
import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.core.util.PasswordTool;
import com.example.shop.public_interface.auth.JwtResponseDto;
import com.example.shop.public_interface.auth.LoginDto;
import com.example.shop.public_interface.client.ClientCreateDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ClientService clientService;
    private final TokenService tokenService;

    public JwtResponseDto login(@NonNull LoginDto dto) {
        final ClientEntity client = clientService.getByEmail(dto.email())
                .orElseThrow(() -> new ExceptionInApplication("Неверная почта или пароль", ExceptionType.NOT_FOUND));

        if (PasswordTool.isCorrectPassword(client.password(), dto.password())) {
            var dataForGenerateToken = forGenerateToken(client);
            return tokenService.getTokens(dataForGenerateToken);
        }

        throw new ExceptionInApplication("Неверная почта или пароль", ExceptionType.NOT_FOUND);
    }

    public JwtResponseDto register(@NonNull ClientCreateDto dto) {
        clientService.createClient(dto);
        var loginDto = new LoginDto(dto.email(), dto.password());

        return login(loginDto);
    }

    public JwtResponseDto getAccessToken(@NonNull String refreshToken) {
        tokenService.checkRefreshToken(refreshToken);

        final String clientId = tokenService.getClientIdInRefreshToken(refreshToken);

        final ClientEntity client = clientService.getByClientId(clientId)
                .orElseThrow(() -> new ExceptionInApplication("Клиент не найден", ExceptionType.NOT_FOUND));
        var dataForGenerateToken = forGenerateToken(client);

        return tokenService.getAccessToken(dataForGenerateToken);
    }

    public JwtResponseDto refresh(@NonNull String refreshToken) {
        tokenService.checkRefreshToken(refreshToken);

        final String tokenId = tokenService.getTokenIdInRefreshToken(refreshToken);
        final String clientId = tokenService.getClientIdInRefreshToken(refreshToken);

        tokenService.deleteRefreshToken(tokenId);

        final ClientEntity client = clientService.getByClientId(clientId)
                .orElseThrow(() -> new ExceptionInApplication("Клиент не найден", ExceptionType.NOT_FOUND));
        var dataForGenerateToken = forGenerateToken(client);

        return tokenService.getTokens(dataForGenerateToken);
    }

    public void logout(@NonNull String refreshToken) {
        tokenService.checkRefreshToken(refreshToken);

        final String tokenId = tokenService.getTokenIdInRefreshToken(refreshToken);

        tokenService.deleteRefreshToken(tokenId);
    }

    private DataForGenerateToken forGenerateToken(ClientEntity entity) {
        return new DataForGenerateToken(entity.clientId().toString());
    }

}
