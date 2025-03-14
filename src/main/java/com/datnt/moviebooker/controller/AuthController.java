package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthService.AuthResponse> login(@RequestBody LoginRequest request) {
        var response = authService.login(request.username(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequest request) {
        var newAccessToken = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    public record LoginRequest(String username, String password) {}

    public record RefreshTokenRequest(String refreshToken) {}
}
