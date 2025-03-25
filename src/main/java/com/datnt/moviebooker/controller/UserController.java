package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.constant.Role;
import com.datnt.moviebooker.dto.UserRequest;
import com.datnt.moviebooker.dto.UserResponse;
import com.datnt.moviebooker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(value = "role", required = false) Role role,
            Pageable pageable) {
        Page<UserResponse> users = userService.getUsers(role, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Admin add new admin, user register
    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        boolean isAdmin = userService.isCurrentUserAdmin();
        UserResponse createdUser = userService.createUser(request, isAdmin);
        return ResponseEntity.ok(createdUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
