package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "creens")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Name can not null")
    @Size(max = 50, min = 5, message = "Name must be between 5 and 50 characters")
    private String name;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    @NotNull(message = "Theater can not null")
    private Theater theater;
}
