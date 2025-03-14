package com.datnt.moviebooker.security;

import com.datnt.moviebooker.service.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final RefreshTokenService refreshTokenService;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ Generate Access Token
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);

        return buildToken(claims, jwtExpiration);
    }

    // ✅ Generate Token từ Refresh Token
    public String generateTokenFromRefreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .filter(token -> !token.isExpired())
                .map(token -> generateToken(
                        token.getUser().getUsername(),
                        token.getUser().getRole().toString()
                ))
                .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));
    }

    // ✅ Lấy username từ token
    public String getUsernameFromToken(String token) {
        return parseClaims(token).get("username", String.class);
    }

    // ✅ Lấy role từ token
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // ✅ Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Check token hết hạn
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    // 🔥 Private method parse token
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // 🔥 Private method build token
    private String buildToken(Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(claims.get("username").toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .claims(claims)
                .signWith(getSigningKey())
                .compact();
    }
}
