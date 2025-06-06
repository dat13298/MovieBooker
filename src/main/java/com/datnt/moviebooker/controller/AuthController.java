package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.AuthResponse;
import com.datnt.moviebooker.dto.LoginRequest;
import com.datnt.moviebooker.dto.RefreshTokenRequest;
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        var response = authService.login(request.username(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        var newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }
}
