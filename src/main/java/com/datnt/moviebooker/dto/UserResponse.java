package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Timestamp createdAt;
}
