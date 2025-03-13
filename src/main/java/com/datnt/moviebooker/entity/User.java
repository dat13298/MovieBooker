package com.datnt.moviebooker.entity;

import com.datnt.moviebooker.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class User {

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

    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Email can not null")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    @Email(message = "Email invalid")
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role can not null")
    private Role role;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Timestamp createdAt;
}
