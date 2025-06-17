package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.ShowTimeRequest;
import com.datnt.moviebooker.dto.ShowTimeResponse;
import com.datnt.moviebooker.entity.Movie;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.ShowTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowTimeMapper {
    private final MovieMapper movieMapper;

    public ShowTime toEntity(ShowTimeRequest request, Movie movie, Screen screen) {
        return ShowTime.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .presentation(request.presentation())
                .build();
    }

    public ShowTimeResponse toResponse(ShowTime showTime) {
        return new ShowTimeResponse(
                showTime.getId(),
                movieMapper.toResponse(showTime.getMovie()),
                showTime.getScreen().getId(),
                showTime.getStartTime(),
                showTime.getPresentation()
        );
    }
}
