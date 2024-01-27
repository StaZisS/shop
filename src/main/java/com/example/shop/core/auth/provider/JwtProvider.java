package com.example.shop.core.auth.provider;

import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SignatureException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final long jwtAccessTtlSecond;
    private final long jwtRefreshTtlSecond;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
            @Value("${jwt.ttl.access}") Long jwtAccessTtlSecond,
            @Value("${jwt.ttl.refresh}") Long jwtRefreshTtlSecond
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.jwtAccessTtlSecond = jwtAccessTtlSecond;
        this.jwtRefreshTtlSecond = jwtRefreshTtlSecond;
    }

    public String generateAccessToken(@NonNull DataForGenerateToken data) {
        final Date accessExpiration = getDateWithPlus(jwtAccessTtlSecond);
        return Jwts.builder()
                .subject(data.clientId())
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(@NonNull DataForGenerateToken data) {
        final Date refreshExpiration = getDateWithPlus(jwtRefreshTtlSecond);
        return Jwts.builder()
                .subject(data.clientId())
                .expiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public void validateAccessToken(@NonNull String accessToken) {
        validateToken(accessToken, jwtAccessSecret);
    }

    public void validateRefreshToken(@NonNull String refreshToken) {
        validateToken(refreshToken, jwtRefreshSecret);
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void validateToken(@NonNull String token, @NonNull SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parse(token);
        } catch (ExpiredJwtException expEx) {
            throw new ExceptionInApplication("Token expired", ExceptionType.INVALID);
        } catch (UnsupportedJwtException unsEx) {
            throw new ExceptionInApplication("Unsupported jwt", ExceptionType.INVALID);
        } catch (MalformedJwtException mjEx) {
            throw new ExceptionInApplication("Malformed jwt", ExceptionType.INVALID);
        } catch (Exception e) {
            throw new ExceptionInApplication("Invalid token", ExceptionType.INVALID);
        }
    }

    private Date getDateWithPlus(long delta) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusSeconds(delta)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Date.from(refreshExpirationInstant);
    }
}
