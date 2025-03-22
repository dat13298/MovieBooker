package com.datnt.moviebooker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendBookingStatus(String bookingId, String status) {
        messagingTemplate.convertAndSend("/topic/booking-status/" + bookingId, status);
//        System.out.println("Sending booking status: " + status);
//        messagingTemplate.convertAndSend("/topic/booking-status", status);
    }
}
