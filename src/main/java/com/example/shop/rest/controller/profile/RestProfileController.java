package com.example.shop.rest.controller.profile;

import com.example.shop.core.auth.provider.JwtProvider;
import com.example.shop.core.client.service.ClientService;
import com.example.shop.public_interface.client.ClientProfileDto;
import com.example.shop.rest.util.JwtTools;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class RestProfileController {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final ClientService clientService;
    private final JwtProvider jwtProvider;

    @GetMapping
    public ClientProfileDto getProfile(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        var token = JwtTools.getTokenFromHeader(accessToken);
        var clientId = jwtProvider.getAccessTokenClientId(token);
        return clientService.getProfile(clientId);
    }
}
