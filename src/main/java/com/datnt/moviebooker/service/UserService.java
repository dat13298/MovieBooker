package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.Role;
import com.datnt.moviebooker.dto.AdminCreateUserRequest;
import com.datnt.moviebooker.dto.UserRegisterRequest;
import com.datnt.moviebooker.dto.UserResponse;
import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.mapper.UserMapper;
import com.datnt.moviebooker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    public UserResponse registerUser(UserRegisterRequest request) {
        return createUserInternal(request, Role.ROLE_USER);
    }

    public UserResponse createUserByAdmin(AdminCreateUserRequest request) {
        return createUserInternal(request, request.getRole());
    }

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

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponse(user);
    }

    private UserResponse createUserInternal(UserRegisterRequest request, Role role) {
        User user = userMapper.toEntity(request, role);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
