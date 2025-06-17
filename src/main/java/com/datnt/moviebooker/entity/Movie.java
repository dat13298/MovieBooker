package com.datnt.moviebooker.entity;

import com.datnt.moviebooker.constant.MovieStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, name = "movie_name")
    @NotNull(message = "Name can not null")
    @Size(max = 50, min = 1, message = "Name must be between 1 and 50 characters")
    private String movieName;

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

    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    @Column(nullable = false, name = "director")
    @NotNull(message = "Director can not null")
    @Size(max = 50, min = 1, message = "Director must be between 1 and 50 characters")
    private String director;

    @Column(nullable = false, name = "actors")
    @NotNull(message = "Actors can not null")
    @Size(max = 500, min = 1, message = "Actors must be between 1 and 100 characters")
    private String actors;

    @Column(nullable = false, name = "movie_type")
    @NotNull(message = "Movie type can not null")
    @Size(max = 30, min = 1, message = "Movie type must be between 1 and 30 characters")
    private String movieType; // e.g., Action, Comedy, Drama, etc.

    @Column(nullable = false, name = "language")
    @NotNull(message = "Language can not null")
    @Size(max = 30, min = 1, message = "Language must be between 1 and 30 characters")
    private String language; // e.g., English, Vietnamese, etc.

    @Column(name = "premiere_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate premiereDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "movie_status")
    private MovieStatus movieStatus; // e.g., COMING_SOON, NOW_SHOWING, ENDED

    @Column(name = "screen_type")
    @NotNull(message = "Screen type can not null")
    @Size(max = 20, min = 1, message = "Screen type must be between 1 and 20 characters")
    private String screenType; // e.g., 2D, 3D, IMAX
}
