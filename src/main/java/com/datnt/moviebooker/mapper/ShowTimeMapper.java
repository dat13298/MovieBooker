package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.ShowTimeRequest;
import com.datnt.moviebooker.dto.ShowTimeResponse;
import com.datnt.moviebooker.entity.Movie;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.ShowTime;
import org.springframework.stereotype.Component;

@Component
public class ShowTimeMapper {

    public ShowTime toEntity(ShowTimeRequest request, Movie movie, Screen screen) {
        return ShowTime.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .price(request.price())
                .build();
    }

    public ShowTimeResponse toResponse(ShowTime showTime) {
        return new ShowTimeResponse(
                showTime.getId(),
                showTime.getMovie().getId(),
                showTime.getScreen().getId(),
                showTime.getStartTime(),
                showTime.getPrice()
        );
    }
}
