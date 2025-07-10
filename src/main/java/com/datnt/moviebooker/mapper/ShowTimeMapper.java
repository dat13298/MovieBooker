package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.ShowTimeRequest;
import com.datnt.moviebooker.dto.ShowTimeResponse;
import com.datnt.moviebooker.entity.Movie;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.ShowTime;
import com.datnt.moviebooker.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowTimeMapper {
    private final MovieMapper movieMapper;
    private final ScreenMapper screenMapper;
    private final SeatRepository seatRepository;

    public ShowTime toEntity(ShowTimeRequest request, Movie movie, Screen screen) {
        return ShowTime.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .presentation(request.presentation())
                .build();
    }

    public ShowTimeResponse toResponse(ShowTime showTime) {
        int seatCount = seatRepository.countByShowTime_Id(showTime.getId());
        return new ShowTimeResponse(
                showTime.getId(),
                movieMapper.toResponse(showTime.getMovie()),
                screenMapper.toResponse(showTime.getScreen()),
                showTime.getStartTime(),
                showTime.getEndTime(),
                showTime.getPresentation(),
                seatCount
        );
    }
}
