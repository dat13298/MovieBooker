package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "theaters")
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Name can not null")
    @Size(max = 50, min = 5, message = "Name must be between 5 and 50 characters")
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Location can not null")
    @Size(max = 50, min = 5, message = "Location must be between 5 and 50 characters")
    private String location;
}
