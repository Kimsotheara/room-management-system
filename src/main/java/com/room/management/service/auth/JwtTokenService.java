package com.room.management.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtTokenService {

    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400}")
    private long expirationSeconds;

    @Value("${app.jwt.refresh-expiration:604800}")
    private long refreshExpirationSeconds;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, String username, String email) {
        return buildToken(userId, username, email, TOKEN_TYPE_ACCESS, expirationSeconds);
    }

    public String generateRefreshToken(Long userId, String username, String email) {
        return buildToken(userId, username, email, TOKEN_TYPE_REFRESH, refreshExpirationSeconds);
    }

    private String buildToken(Long userId, String username, String email,
                               String tokenType, long expirationSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .claims(Map.of(
                        "userId", userId,
                        "username", username,
                        "email", email != null ? email : "",
                        "tokenType", tokenType
                ))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractTokenType(String token) {
        return (String) extractAllClaims(token).get("tokenType");
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        if (userId instanceof Integer) return ((Integer) userId).longValue();
        if (userId instanceof Long) return (Long) userId;
        return null;
    }

    public long getAccessTokenExpirationSeconds() {
        return expirationSeconds;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshExpirationSeconds;
    }
}
