package com.example.shop.auth;

import com.example.shop.core.auth.provider.JwtProvider;
import com.example.shop.core.auth.repository.RefreshRepository;
import com.example.shop.core.auth.service.AuthService;
import com.example.shop.core.auth.service.TokenService;
import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.core.util.PasswordTool;
import com.example.shop.public_interface.auth.JwtResponseDto;
import com.example.shop.public_interface.auth.LoginDto;
import com.example.shop.public_interface.client.ClientCreateDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {AuthIntegrationTestConfiguration.class})
public class AuthServiceIntegrationTest {
    private static final int PORT = 6380;
    @Container
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:latest")).withExposedPorts(PORT);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(PORT).toString());
    }

    private AuthService authService;
    private ClientService clientService;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshRepository refreshRepository;

    @BeforeEach
    public void setup() {
        clientService = mock(ClientService.class);

        var tokenService = new TokenService(refreshRepository, jwtProvider);
        authService = new AuthService(clientService, tokenService);
    }

    @Test
    public void registerAndLogin() {
        var password = "12345678";
        var registerDto = new ClientCreateDto(
                "Gordey",
                "ggwp@mail.ru",
                password,
                OffsetDateTime.now(),
                "MALE"
        );

        registerClient(registerDto);

        var loginDto = new LoginDto(registerDto.email(), password);
        authService.login(loginDto);
    }

    @Test
    public void getAccessToken() {
        var password = "12345678";
        var registerDto = new ClientCreateDto(
                "Gordey",
                "ggwp1@mail.ru",
                password,
                OffsetDateTime.now(),
                "MALE"
        );

        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                registerDto.name(),
                registerDto.email(),
                PasswordTool.getHashPassword(registerDto.password()),
                registerDto.birthDate(),
                registerDto.gender(),
                OffsetDateTime.now()
        );

        when(clientService.getByEmail(registerDto.email()))
                .thenReturn(Optional.of(clientEntity));

        var jwtTokens = authService.register(registerDto);

        when(clientService.getByClientId(clientEntity.clientId().toString()))
                .thenReturn(Optional.of(clientEntity));

        var jwtAccessToken = authService.getAccessToken(jwtTokens.refreshToken());

        assertNotNull(jwtAccessToken.accessToken());
        assertNull(jwtAccessToken.refreshToken());
    }

    @Test
    public void refresh() {
        var password = "12345678";
        var registerDto = new ClientCreateDto(
                "Gordey",
                "ggwp2@mail.ru",
                password,
                OffsetDateTime.now(),
                "MALE"
        );

        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                registerDto.name(),
                registerDto.email(),
                PasswordTool.getHashPassword(registerDto.password()),
                registerDto.birthDate(),
                registerDto.gender(),
                OffsetDateTime.now()
        );

        when(clientService.getByEmail(registerDto.email()))
                .thenReturn(Optional.of(clientEntity));

        var jwtTokens = authService.register(registerDto);

        when(clientService.getByClientId(clientEntity.clientId().toString()))
                .thenReturn(Optional.of(clientEntity));

        var newJwtTokens = authService.refresh(jwtTokens.refreshToken());

        assertNotNull(newJwtTokens.accessToken());
        assertNotNull(newJwtTokens.refreshToken());
    }

    @Test
    public void logout() {
        var password = "12345678";
        var registerDto = new ClientCreateDto(
                "Gordey",
                "ggwp3@mail.ru",
                password,
                OffsetDateTime.now(),
                "MALE"
        );

        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                registerDto.name(),
                registerDto.email(),
                PasswordTool.getHashPassword(registerDto.password()),
                registerDto.birthDate(),
                registerDto.gender(),
                OffsetDateTime.now()
        );

        when(clientService.getByEmail(registerDto.email()))
                .thenReturn(Optional.of(clientEntity));

        var jwtTokens = authService.register(registerDto);

        authService.logout(jwtTokens.refreshToken());

        when(clientService.getByClientId(clientEntity.clientId().toString()))
                .thenReturn(Optional.of(clientEntity));

        assertThrows(ExceptionInApplication.class, () -> authService.getAccessToken(jwtTokens.refreshToken()));
    }

    @Test
    public void logoutAlreadyLogout() {
        var password = "12345678";
        var registerDto = new ClientCreateDto(
                "Gordey",
                "ggwp4@mail.ru",
                password,
                OffsetDateTime.now(),
                "MALE"
        );

        var jwtTokens = registerClient(registerDto);

        authService.logout(jwtTokens.refreshToken());

        assertThrows(ExceptionInApplication.class, () -> authService.logout(jwtTokens.refreshToken()));
    }

    private JwtResponseDto registerClient(ClientCreateDto dto) {
        var clientEntity = new ClientEntity(
                UUID.randomUUID(),
                dto.name(),
                dto.email(),
                PasswordTool.getHashPassword(dto.password()),
                dto.birthDate(),
                dto.gender(),
                OffsetDateTime.now()
        );

        when(clientService.getByEmail(dto.email()))
                .thenReturn(Optional.of(clientEntity));

        return authService.register(dto);
    }
}
