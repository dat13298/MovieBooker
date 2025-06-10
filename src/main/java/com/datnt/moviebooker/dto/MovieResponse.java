package com.datnt.moviebooker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private int duration;
    private String rating;
    private String imageUrl;
}
