package com.example.shop.core.auth.repository;

import com.example.shop.core.auth.entity.RefreshTokenEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import javax.annotation.PostConstruct;

@Repository
@RequiredArgsConstructor
public class RefreshRepositoryImpl implements RefreshRepository {
    private static final String KEY = "RefreshToken";

    private final RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, RefreshTokenEntity> hashOperations;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void saveRefreshToken(@NonNull RefreshTokenEntity entity) {
        hashOperations.put(KEY, entity.getClientId(), entity);
    }

    @Override
    public Optional<RefreshTokenEntity> getRefreshTokenByClientId(@NonNull String clientId) {
        return Optional.ofNullable(hashOperations.get(KEY, clientId));
    }

    @Override
    public void deleteRefreshToken(@NonNull String clientId) {
        hashOperations.delete(KEY, clientId);
    }
}
