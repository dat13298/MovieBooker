package com.datnt.moviebooker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws ServletException, IOException {

        // Get token from header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            assert filterChain != null;
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Get username from token
        String username = jwtService.getUsernameFromToken(token);

        // Authenticate user
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details
            var userDetails = userDetailsService.loadUserByUsername(username);
            // Validate token and set authentication
            if (jwtService.validateToken(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication to context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        assert filterChain != null;
        filterChain.doFilter(request, response);
    }
}
