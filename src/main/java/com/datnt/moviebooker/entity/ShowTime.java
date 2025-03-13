package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "show_times")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @NotNull(message = "Movie can not null")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "screen_id")
    @NotNull(message = "Screen can not null")
    private Screen screen;

    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start Time can not null")
    private Timestamp startTime;

    @Column(nullable = false)
    @NotNull(message = "Price can not null")
    @Min(value = 0)
    private BigDecimal price;
}
