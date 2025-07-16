package com.datnt.moviebooker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String username;
    private String email;
    private String phone;
    private String gender;
    private String dob;
}
