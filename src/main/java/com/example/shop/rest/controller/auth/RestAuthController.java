package com.example.shop.rest.controller.auth;

import com.example.shop.core.auth.service.AuthService;
import com.example.shop.public_interface.auth.JwtResponseDto;
import com.example.shop.public_interface.auth.LoginDto;
import com.example.shop.public_interface.client.ClientCreateDto;
import com.example.shop.rest.controller.mapper.RequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RestAuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponseDto login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/register")
    public JwtResponseDto register(@RequestBody RegisterDto dto) {
        final ClientCreateDto clientCreateDto = RequestMapper.mapRequestToDto(dto);
        return authService.register(clientCreateDto);
    }

    @PostMapping("/token")
    public JwtResponseDto getAccessToken(@RequestBody RefreshJwtRequest dto) {
        return authService.getAccessToken(dto.refreshToken());
    }

    @PostMapping("/refresh")
    public JwtResponseDto refresh(@RequestBody RefreshJwtRequest dto) {
        return authService.refresh(dto.refreshToken());
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshJwtRequest dto) {
        authService.logout(dto.refreshToken());
    }
}
