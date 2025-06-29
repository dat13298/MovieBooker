package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserRegisterRequest {

    @NotNull
    @Size(min = 5, max = 50)
    private String username;

    @NotNull
    @Size(min = 8, max = 60)
    private String password;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date DoB;

    @NotNull
    private Gender gender;

    @NotNull
    @Pattern(
            regexp = "^(0[1-9][0-9]{8})$",
            message = "Phone number must be 10 digits and start with 0"
    )
    private String phoneNumber;

    @NotNull
    @Size(min = 5, max = 100)
    @Email
    private String email;
}
