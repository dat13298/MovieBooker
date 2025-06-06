package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.Role;
import com.datnt.moviebooker.dto.UserRequest;
import com.datnt.moviebooker.dto.UserResponse;
import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.mapper.UserMapper;
import com.datnt.moviebooker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isAdmin(Long userId) {
        return findById(userId).getRole().equals(Role.ROLE_ADMIN);
    }

    public Page<UserResponse> getUsers(Role role, Pageable pageable) {
        Page<User> users = (role != null) ? userRepository.findUsersByRole(role, pageable) : userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponse(user);
    }

    public UserResponse createUser(UserRequest request, boolean isAdmin) {
        Role requestedRole = request.getRole() != null ? request.getRole() : Role.ROLE_USER;

        if (isAdmin && requestedRole != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Admin can only add another admin!");
        }

        Role userRole = isAdmin ? requestedRole : Role.ROLE_USER;

        User newUser = userMapper.toEntity(request, userRole);
        newUser.setRole(userRole);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(newUser);

        return userMapper.toResponse(savedUser);
    }


    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.ROLE_ADMIN.name()));
    }

    public UserResponse createUserAsAdmin(UserRequest request) {
        // Nếu không gửi role, mặc định là USER
        Role roleToAssign = request.getRole() != null ? request.getRole() : Role.ROLE_USER;

        // Sử dụng mapper overload để truyền role chính xác
        User newUser = userMapper.toEntity(request, roleToAssign);

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponse(savedUser);
    }


}
