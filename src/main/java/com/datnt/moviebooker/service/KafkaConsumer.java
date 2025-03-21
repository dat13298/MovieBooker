package com.datnt.moviebooker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final BookingService bookingService;
    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "seat_booking", groupId = "movie_booking_group")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }

    @KafkaListener(topics = "seat_booking_queue", groupId = "movie_booking_group")
    public void handleBooking(String message) {
        String[] parts = message.split(":");
        try {
            String bookingId = parts[0];
            Long showTimeId = Long.parseLong(parts[1]);
            String seatIdsStr = parts[2].replace("[", "").replace("]", "");
            List<Long> seatIds = Stream.of(seatIdsStr.split(", "))
                    .map(Long::parseLong)
                    .toList();
            Long userId = Long.parseLong(parts[3]);

            bookingService.processBooking(showTimeId, seatIds, userId);

            redisTemplate.opsForValue().set(bookingId, "SUCCESS", 30, TimeUnit.SECONDS);
        } catch (RuntimeException e) {
            redisTemplate.opsForValue().set(parts[0], "FAILED: " + e.getMessage(), 30, TimeUnit.SECONDS);
            throw e;
        }
    }
}
