package com.example.shop.rest.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshJwtRequest(
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
