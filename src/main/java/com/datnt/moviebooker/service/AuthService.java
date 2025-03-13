package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.repository.UserRepository;
import com.datnt.moviebooker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(username).orElseThrow();

        String accessToken = jwtUtil.generateToken(username);
        var refreshToken = refreshTokenService.createRefreshToken(user);

        return "Access Token: " + accessToken + "\nRefresh Token: " + refreshToken.getToken();
    }

    public String refreshAccessToken(String refreshToken) {
        return jwtUtil.generateTokenFromRefreshToken(refreshToken);
    }

}
