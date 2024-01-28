package com.example.shop.core.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class RefreshTokenEntity implements Serializable {
    private String clientId;
    private String refreshToken;
    @TimeToLive
    private long expiration;
}
