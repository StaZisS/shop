package com.example.shop.auth;

import com.example.shop.core.auth.entity.RefreshTokenEntity;
import com.example.shop.core.auth.provider.DataForGenerateToken;
import com.example.shop.core.auth.provider.JwtProvider;
import com.example.shop.core.auth.repository.RefreshRepository;
import com.example.shop.core.auth.repository.RefreshRepositoryImpl;
import com.example.shop.core.auth.service.AuthService;
import com.example.shop.core.auth.service.TokenService;
import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.core.util.PasswordTool;
import com.example.shop.public_interface.auth.LoginDto;
import com.example.shop.public_interface.client.ClientCreateDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceUnitTest {
    private final static String KEY_REFRESH = "qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==";
    private final static String KEY_ACCESS = "zL1HB3Pch05Avfynovxrf/kpF9O2m4NCWKJUjEp27s9J2jEG3ifiKCGylaZ8fDeoONSTJP/wAzKawB8F9rOMNg==";
    private final static long TTL_REFRESH = 2592000;
    private final static long TTL_ACCESS = 600;

    private AuthService authService;
    private ClientService clientService;
    private RefreshRepository refreshRepository;
    private JwtProvider jwtProvider;

    @BeforeEach
    public void setup() {
        clientService = mock(ClientService.class);
        refreshRepository = mock(RefreshRepositoryImpl.class);
        jwtProvider = new JwtProvider(KEY_ACCESS, KEY_REFRESH, TTL_ACCESS, TTL_REFRESH);
        var tokenService = new TokenService(refreshRepository, jwtProvider);

        authService = new AuthService(clientService, tokenService);
    }

    @Test
    public void loginSuccess() {
        var password = "12345678";
        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                "Gordey",
                "ggwp@mail.ru",
                PasswordTool.getHashPassword(password),
                OffsetDateTime.now(),
                "MALE",
                OffsetDateTime.now()
        );
        var loginDto = new LoginDto(clientEntity.email(), password);

        when(clientService.getByEmail(loginDto.email()))
                .thenReturn(Optional.of(clientEntity));

        var jwtTokens = authService.login(loginDto);

        assertNotNull(jwtTokens.accessToken());
        assertNotNull(jwtTokens.refreshToken());
    }

    @Test
    public void loginUserNotExist() {
        var loginDto = new LoginDto("ggwp@mail.ru", "12345678");

        when(clientService.getByEmail(loginDto.email()))
                .thenReturn(Optional.empty());

        assertThrows(ExceptionInApplication.class, () -> authService.login(loginDto));
    }

    @Test
    public void loginIncorrectPassword() {
        var password = "12345678";
        var incorrectPassword = "123456789qqqqqq";

        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                "Gordey",
                "ggwp@mail.ru",
                PasswordTool.getHashPassword(password),
                OffsetDateTime.now(),
                "MALE",
                OffsetDateTime.now()
        );
        var loginDto = new LoginDto(clientEntity.email(), incorrectPassword);

        when(clientService.getByEmail(loginDto.email()))
                .thenReturn(Optional.of(clientEntity));

        assertThrows(ExceptionInApplication.class, () -> authService.login(loginDto));
    }

    @Test
    public void register() {
        var password = "12345678";
        var registerDto = new ClientCreateDto(
                "Gordey",
                "ggwp@mail.ru",
                password,
                OffsetDateTime.now(),
                "MALE"
        );

        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                registerDto.name(),
                registerDto.email(),
                PasswordTool.getHashPassword(password),
                registerDto.birthDate(),
                registerDto.gender(),
                OffsetDateTime.now()
        );

        when(clientService.getByEmail(registerDto.email()))
                .thenReturn(Optional.of(clientEntity));

        var jwtTokens = authService.register(registerDto);

        assertNotNull(jwtTokens.accessToken());
        assertNotNull(jwtTokens.refreshToken());
    }

    @Test
    public void registerClientWithExistEmail() {
        var registerDto = new ClientCreateDto(
                "Gordey",
                "ggwp@mail.ru",
                "12345678",
                OffsetDateTime.now(),
                "MALE"
        );

        when(clientService.createClient(registerDto))
                .thenThrow(new ExceptionInApplication("Пользователь с такой почтой уже существует", ExceptionType.ALREADY_EXISTS));

        assertThrows(ExceptionInApplication.class, () -> authService.register(registerDto));
    }

    @Test
    public void getAccessToken() {
        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                "Gordey",
                "ggwp@mail.ru",
                "12345678",
                OffsetDateTime.now(),
                "MALE",
                OffsetDateTime.now()
        );
        final String clientId = clientEntity.clientId().toString();
        var dataForGenerateToken = new DataForGenerateToken(clientId);
        var refreshToken = jwtProvider.generateRefreshToken(dataForGenerateToken);
        var refreshTokenEntity = new RefreshTokenEntity(
                jwtProvider.getRefreshClaims(refreshToken).getId(),
                refreshToken,
                jwtProvider.getRefreshTokenTtl()
        );

        when(refreshRepository.getRefreshTokenById(refreshTokenEntity.getTokenId()))
                .thenReturn(Optional.of(refreshTokenEntity));
        when(clientService.getByClientId(UUID.fromString(clientId)))
                .thenReturn(Optional.of(clientEntity));

        var jwtToken = authService.getAccessToken(refreshToken);

        assertNotNull(jwtToken.accessToken());
        assertNull(jwtToken.refreshToken());
    }

    @Test
    public void refresh() {
        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                "Gordey",
                "ggwp@mail.ru",
                "12345678",
                OffsetDateTime.now(),
                "MALE",
                OffsetDateTime.now()
        );
        final String clientId = clientEntity.clientId().toString();
        var dataForGenerateToken = new DataForGenerateToken(clientId);
        var refreshToken = jwtProvider.generateRefreshToken(dataForGenerateToken);
        var refreshTokenEntity = new RefreshTokenEntity(
                jwtProvider.getRefreshClaims(refreshToken).getId(),
                refreshToken,
                jwtProvider.getRefreshTokenTtl()
        );

        when(refreshRepository.getRefreshTokenById(refreshTokenEntity.getTokenId()))
                .thenReturn(Optional.of(refreshTokenEntity));
        when(clientService.getByClientId(UUID.fromString(clientId)))
                .thenReturn(Optional.of(clientEntity));

        var jwtTokens = authService.refresh(refreshToken);

        assertNotNull(jwtTokens.accessToken());
        assertNotNull(jwtTokens.refreshToken());
    }
}
