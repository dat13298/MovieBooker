package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.MovieStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private int duration;
    private String rating;
    private String imageUrl;
    private String director;
    private String actors;
    private String movieType;
    private String language;
    private LocalDate premiereDate;
    private MovieStatus movieStatus;
    private String screenType;
}
