package com.datnt.moviebooker.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String sendSeatBookingRequest(Long showTimeId, List<Long> seatIds, Long userId) {
        String bookingId = UUID.randomUUID().toString();
        String message = bookingId + ":" + showTimeId + ":" + seatIds + ":" + userId;
        kafkaTemplate.send("seat_booking_queue", message);
        return bookingId;
    }
}
