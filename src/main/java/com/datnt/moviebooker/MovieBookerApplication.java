package com.datnt.moviebooker;

import com.datnt.moviebooker.config.VnPayConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
@EnableConfigurationProperties(VnPayConfig.class)
@EnableJpaRepositories(basePackages = "com.datnt.moviebooker.repository")
public class MovieBookerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieBookerApplication.class, args);
    }

}
