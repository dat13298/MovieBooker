package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.SeatRequest;
import com.datnt.moviebooker.dto.SeatResponse;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatResponse toResponse(Seat seat) {
        SeatResponse response = new SeatResponse();
        response.setId(seat.getId());
        response.setSeatNumber(seat.getSeatNumber());
        response.setScreenId(seat.getScreen().getId());
        return response;
    }

    public Seat toEntity(SeatRequest request, Screen screen) {
        return Seat.builder()
                .seatNumber(request.getSeatNumber())
                .screen(screen)
                .build();
    }

    public void updateEntity(Seat seat, SeatRequest request, Screen screen) {
        seat.setSeatNumber(request.getSeatNumber());
        seat.setScreen(screen);
    }
}
