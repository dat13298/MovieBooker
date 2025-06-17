package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.Gender;
import com.datnt.moviebooker.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String phoneNumber;
    private Date DoB;
    private Gender gender;
}
