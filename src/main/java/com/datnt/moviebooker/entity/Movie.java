package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotNull(message = "Title can not null")
    @Size(max = 50, min = 1, message = "Title must be between 1 and 50 characters")
    private String title;

    @Column(nullable = false)
    @NotNull(message = "Duration can not null")
    private Integer duration;

    @Column(nullable = false, length = 10)
    @NotNull(message = "Rating can not null")
    @Size(min = 5, max = 10, message = "Title must be between 5 and 10 characters")
    private String rating;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Description can not null")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Release Date can not null")
    private Date releaseDate;
}
