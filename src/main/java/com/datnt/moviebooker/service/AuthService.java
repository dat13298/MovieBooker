package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.AuthResponse;
import com.datnt.moviebooker.entity.User;
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
    private final UserService userService;

    public AuthResponse login(String username, String password) {
        try {
            // Authenticate user with username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            // Set authentication to SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate access token and refresh token
        User user = userService.findByUsername(username);
        String accessToken = jwtService.generateToken(user.getUsername(), user.getRole().toString(), user.getEmail(), user.getPhoneNumber(), user.getGender().toString(), user.getDoB().toString());
        var refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getUsername(), user.getEmail(), user.getPhoneNumber(), user.getRole().toString());
    }

    // Refresh access token with refresh token
    public String refreshAccessToken(String refreshToken) {
        // Find refresh token in database
        var tokenEntity = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Check if refresh token is expired
        if (refreshTokenService.isTokenExpired(tokenEntity)) {
            throw new RuntimeException("Refresh token expired");
        }

        // Generate new access token
        var user = tokenEntity.getUser();
        return jwtService.generateToken(user.getUsername(), user.getRole().toString(), user.getEmail(), user.getPhoneNumber(), user.getGender().toString(), user.getDoB().toString());
    }

    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }


    // Get current user id from SecurityContext
    public Long getCurrentUserId() {
        // Get principal from SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if principal is instance of UserDetails (User is authenticated)
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return getUserIdFromUsername(username);
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    // Get user id from username (used in getCurrentUserId)
    private Long getUserIdFromUsername(String username) {
        return userService.findByUsername(username).getId();
    }
}
