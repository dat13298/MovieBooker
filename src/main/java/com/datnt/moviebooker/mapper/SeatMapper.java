package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.dto.SeatRequest;
import com.datnt.moviebooker.dto.SeatResponse;
import com.datnt.moviebooker.entity.Screen;
import com.datnt.moviebooker.entity.Seat;
import com.datnt.moviebooker.entity.ShowTime;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatResponse toResponse(Seat seat) {
        SeatResponse res = new SeatResponse();
        res.setId(seat.getId());
        res.setSeatNumber(seat.getSeatNumber());
        res.setScreenId(seat.getScreen().getId());
        res.setShowTimeId(seat.getShowTime().getId());
        res.setStatus(seat.getStatus());
        res.setPrice(seat.getPrice());
        res.setSeatType(seat.getSeatType());
        res.setRowIdx(seat.getRow());
        res.setColIdx(seat.getCol());
        return res;
    }

    public Seat toEntity(SeatRequest req, Screen screen, ShowTime st) {
        return Seat.builder()
                .seatNumber(req.getSeatNumber())
                .screen(screen)
                .row(req.getRowIdx())
                .col(req.getColIdx())
                .showTime(st)
                .status(req.getStatus() == null ? SeatStatus.AVAILABLE : req.getStatus())
                .price(req.getPrice())
                .seatType(req.getSeatType())
                .build();
    }

    public void updateEntity(Seat seat, SeatRequest req, Screen screen, ShowTime st) {
        seat.setSeatNumber(req.getSeatNumber());
        seat.setScreen(screen);
        seat.setShowTime(st);
        seat.setStatus(req.getStatus());
        seat.setPrice(req.getPrice());
        seat.setSeatType(req.getSeatType());
        seat.setRow(req.getRowIdx());
        seat.setCol(req.getColIdx());
    }
}

