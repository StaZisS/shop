package com.example.shop.rest.controller.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record RegisterDto(
        String name,
        String email,
        String password,
        @JsonProperty("birth_date")
        OffsetDateTime birthDate,
        String gender
) {
}
