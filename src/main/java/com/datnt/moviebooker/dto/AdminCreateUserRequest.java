package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateUserRequest extends UserRegisterRequest {
    @NotNull
    private Role role;
}
