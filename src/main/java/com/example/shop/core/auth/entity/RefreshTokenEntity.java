package com.example.shop.core.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RefreshTokenEntity implements Serializable {
    private String tokenId;
    private String refreshToken;
    @TimeToLive
    private long expiration;
}
