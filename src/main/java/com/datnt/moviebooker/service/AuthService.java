package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.repository.UserRepository;
import com.datnt.moviebooker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(username).orElseThrow();

        String accessToken = jwtService.generateToken(user.getUsername(), user.getRole().toString());
        var refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getUsername(), user.getRole().toString());
    }


    public String refreshAccessToken(String refreshToken) {
        var tokenEntity = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshTokenService.isTokenExpired(tokenEntity)) {
            throw new RuntimeException("Refresh token expired");
        }

        var user = tokenEntity.getUser();
        return jwtService.generateToken(user.getUsername(), user.getRole().toString());
    }

    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return getUserIdFromUsername(username);
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    private Long getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    public record AuthResponse(String accessToken, String refreshToken, String username, String role) {}
}
