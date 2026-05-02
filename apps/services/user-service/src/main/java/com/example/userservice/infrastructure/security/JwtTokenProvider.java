package com.example.userservice.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌提供者
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final long accessTokenExpireSeconds;
    private final long refreshTokenExpireSeconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.access-token-expire-seconds:1800}") long accessTokenExpireSeconds,
            @Value("${jwt.refresh-token-expire-seconds:604800}") long refreshTokenExpireSeconds) {

        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT_SECRET 环境变量必须配置");
        }
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET 长度必须≥32字符（256bits）");
        }
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpireSeconds = accessTokenExpireSeconds;
        this.refreshTokenExpireSeconds = refreshTokenExpireSeconds;
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpireSeconds * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpireSeconds * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从令牌中提取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从令牌中提取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("username", String.class);
    }

    /**
     * 验证访问令牌
     */
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证刷新令牌
     */
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从刷新令牌中提取用户ID
     */
    public Long getUserIdFromRefreshToken(String token) {
        return getUserIdFromToken(token);
    }

    /**
     * 获取刷新令牌过期时间（秒）
     */
    public long getRefreshTokenExpireSeconds() {
        return refreshTokenExpireSeconds;
    }
}