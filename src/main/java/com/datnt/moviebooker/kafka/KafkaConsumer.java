package com.datnt.moviebooker.kafka;

import com.datnt.moviebooker.dto.BookingMessage;
import com.datnt.moviebooker.service.BookingService;
import com.datnt.moviebooker.service.RedisService;
import com.datnt.moviebooker.service.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final BookingService bookingService;
    private final RedisService redisService;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "seat_booking", groupId = "movie_booking_group")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }

    @KafkaListener(topics = "seat_booking_queue", groupId = "movie_booking_group")
    public void handleBooking(String message) {
        try {
            BookingMessage bookingMessage = objectMapper.readValue(message, BookingMessage.class);
            String bookingId = bookingMessage.getBookingId();

            // process booking and save to database
            bookingService.processBooking(
                    bookingMessage.getShowTimeId(),
                    bookingMessage.getSeatIds(),
                    bookingMessage.getUserId()
            );

            // Save booking status to Redis
            redisService.saveData(bookingId, "CONFIRMED", 60, TimeUnit.SECONDS);

            // Notify booking status to WebSocket
            webSocketService.sendBookingStatus(bookingId, "CONFIRMED");

        } catch (Exception e) {
            redisService.saveData(message, "FAILED: " + e.getMessage(), 60, TimeUnit.SECONDS);
            webSocketService.sendBookingStatus(message, "FAILED");
        }
    }
}
