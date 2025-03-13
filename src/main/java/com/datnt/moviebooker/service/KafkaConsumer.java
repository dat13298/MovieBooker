package com.datnt.moviebooker.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "seat_booking", groupId = "movie_booking_group")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
