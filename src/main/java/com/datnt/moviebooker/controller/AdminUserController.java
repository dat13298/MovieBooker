package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.UserRequest;
import com.datnt.moviebooker.dto.UserResponse;
import com.datnt.moviebooker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUserAsAdmin(@Valid @RequestBody UserRequest request) {
        // Admin được quyền tạo user với bất kỳ role
        UserResponse createdUser = userService.createUserAsAdmin(request);
        return ResponseEntity.ok(createdUser);
    }
}
