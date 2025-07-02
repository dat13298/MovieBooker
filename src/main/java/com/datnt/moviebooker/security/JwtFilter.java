package com.datnt.moviebooker.security;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if (
                path.equals("/api/users/register") ||
                        path.startsWith("/api/auth") ||
                        path.startsWith("/swagger-ui") ||
                        path.startsWith("/v3/api-docs") ||
                        path.startsWith("/swagger-resources") ||
                        path.startsWith("/swagger-config") ||
                        path.startsWith("/webjars") ||
                        path.equals("/swagger-ui.html") ||
                        path.equals("/error")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    String role = jwtService.getClaimFromToken(token, "role");
                    var authorities = List.of(new SimpleGrantedAuthority(role));

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            var errorResponse = ApiWrapperResponse.builder()
                    .code(ResponseCode.TOKEN_EXPIRED.getCode())
                    .codeName(ResponseCode.TOKEN_EXPIRED.getName())
                    .message(ResponseCode.TOKEN_EXPIRED.getMessage())
                    .data(null)
                    .build();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            e.printStackTrace();

            var errorResponse = ApiWrapperResponse.builder()
                    .code(ResponseCode.UNAUTHORIZED.getCode())
                    .codeName(ResponseCode.UNAUTHORIZED.getName())
                    .message("Unauthorized")
                    .data(null)
                    .build();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

}
