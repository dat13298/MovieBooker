package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @NotNull(message = "Username cannot be null")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    private String username;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 60, message = "Password must be between 8 and 15 characters")
    private String password;

    @NotNull(message = "Email cannot be null")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    @Email(message = "Invalid email format")
    private String email;

    private Role role; // Admin có thể set role khi thêm admin
}
