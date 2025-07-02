package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.BookingStatusMessage;
import com.datnt.moviebooker.dto.SeatEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendSeatLocked(Long seatId,
                               Long showTimeId,
                               Long userId,
                               long expiresAt) {
        SeatEvent evt = new SeatEvent(
                "LOCKED", seatId, showTimeId, userId, expiresAt, null);
        messagingTemplate.convertAndSend(
                "/topic/showtime/" + showTimeId, evt);
    }

    public void sendSeatReleased(Long seatId,
                                 Long showTimeId) {
        SeatEvent evt = new SeatEvent(
                "RELEASED", seatId, showTimeId, null, null, null);
        messagingTemplate.convertAndSend(
                "/topic/showtime/" + showTimeId, evt);
    }

    public void sendSeatBooked(Long seatId,
                               Long showTimeId,
                               Long bookingId) {
        SeatEvent evt = new SeatEvent(
                "BOOKED", seatId, showTimeId, null, null, bookingId);
        messagingTemplate.convertAndSend(
                "/topic/showtime/" + showTimeId, evt);
    }

    /* ---------- booking-status ---------- */

    public void sendBookingStatus(Long bookingId, String status) {
        messagingTemplate.convertAndSend(
                "/topic/booking-status/" + bookingId,
                new BookingStatusMessage(status));
    }
}

