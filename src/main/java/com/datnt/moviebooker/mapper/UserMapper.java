package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.constant.Role;
import com.datnt.moviebooker.dto.UserRequest;
import com.datnt.moviebooker.dto.UserResponse;
import com.datnt.moviebooker.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    // Chuyển từ UserRequest DTO sang User entity
    public User toEntity(UserRequest request, Role forcedRole) {
        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(forcedRole)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}
