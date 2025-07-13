package com.datnt.moviebooker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate Access Token
    public String generateToken(String username, String role, String email, String phone, String gender, String dob) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("email", email);
        claims.put("phone", phone);
        claims.put("gender", gender);
        claims.put("dob", dob);
        claims.put("role", role);

        return buildToken(claims, jwtExpiration);
    }

    // get username from token
    public String getUsernameFromToken(String token) {
        return parseClaims(token).get("username", String.class);
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // Get username from token
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public String getClaimFromToken(String token, String claimKey) {
        return parseClaims(token).get(claimKey, String.class);
    }


    // Check token Expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    // Private method parse token
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // Private method build token
    private String buildToken(Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);// Set expiration time

        return Jwts.builder()
                .subject(claims.get("username").toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .claims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    // Lấy username trực tiếp từ token trong request hiện tại
    public String getUsernameFromCurrentRequestToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return getUsernameFromToken(token);
        }
        return null;
    }
}
