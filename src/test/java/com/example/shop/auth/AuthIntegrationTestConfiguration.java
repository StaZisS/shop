package com.example.shop.auth;

import com.example.shop.core.auth.config.RedisConfig;
import com.example.shop.core.auth.provider.JwtProvider;
import com.example.shop.core.auth.repository.RefreshRepositoryImpl;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({
        JwtProvider.class,
        RedisConfig.class,
        RefreshRepositoryImpl.class
})
public class AuthIntegrationTestConfiguration {
}
