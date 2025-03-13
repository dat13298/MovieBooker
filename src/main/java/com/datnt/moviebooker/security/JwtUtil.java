package com.datnt.moviebooker.security;

import com.datnt.moviebooker.entity.RefreshToken;
import com.datnt.moviebooker.service.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final RefreshTokenService refreshTokenService;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateTokenFromRefreshToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenService.findByToken(refreshToken);

        if (tokenEntity == null || refreshTokenService.isTokenExpired(tokenEntity)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        return generateToken(tokenEntity.getUser().getUsername());
    }
}
