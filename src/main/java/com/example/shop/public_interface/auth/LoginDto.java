package com.example.shop.public_interface.auth;

public record LoginDto(
        String email,
        String password
) {
}
