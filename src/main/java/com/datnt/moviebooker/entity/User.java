package com.datnt.moviebooker.entity;

import com.datnt.moviebooker.constant.Gender;
import com.datnt.moviebooker.constant.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Username can not null")
    @Size(max = 50, min = 5, message = "Username must be between 5 and 50 characters")
    private String username;

    @Column(nullable = false, unique = true, length = 60)
    @NotNull(message = "Password can not null")
    @Size(min = 8, max = 60, message = "Password must be between 8 and 15 characters")
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    @NotNull(message = "Email can not null")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    @Email(message = "Email invalid")
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role can not null")
    private Role role;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, name = "date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date DoB;

    @NotNull(message = "Gender cannot be null")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Phone number cannot be null")
    @Column(nullable = false, unique = true, name = "phone_number")
    @Pattern(
            regexp = "^(0[1-9][0-9]{8})$",
            message = "Phone number must be 10 digits and start with 0"
    )
    private String phoneNumber;
}
