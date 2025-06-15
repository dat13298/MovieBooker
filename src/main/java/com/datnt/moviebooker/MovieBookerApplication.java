package com.datnt.moviebooker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class MovieBookerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieBookerApplication.class, args);
    }

}
