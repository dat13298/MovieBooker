package com.datnt.moviebooker.controller;


import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.repository.RefreshTokenRepository;
import com.datnt.moviebooker.security.JwtUtil;
import com.datnt.moviebooker.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public String login(@RequestParam String username) {
        String accessToken = jwtUtil.generateToken(username);
        User user = new User(); // Giả sử bạn đã lấy User từ DB
        user.setUsername(username);

        var refreshToken = refreshTokenService.createRefreshToken(user);

        return "Access Token: " + accessToken + "\nRefresh Token: " + refreshToken.getToken();
    }

    @PostMapping("/refresh")
    public String refresh(@RequestParam String refreshToken) {
        var tokenEntity = refreshTokenService.findByToken(refreshToken);

        if (tokenEntity == null || refreshTokenService.isTokenExpired(tokenEntity)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtUtil.generateToken(tokenEntity.getUser().getUsername());

        return "New Access Token: " + newAccessToken;
    }
}
