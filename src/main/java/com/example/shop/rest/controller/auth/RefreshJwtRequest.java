package com.example.shop.rest.controller.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshJwtRequest(
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
