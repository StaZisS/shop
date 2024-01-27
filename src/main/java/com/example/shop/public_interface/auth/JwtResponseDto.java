package com.example.shop.public_interface.auth;

public record JwtResponseDto(
        String accessToken,
        String refreshToken
){
}
