package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.TheaterRequest;
import com.datnt.moviebooker.dto.TheaterResponse;
import com.datnt.moviebooker.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public Theater toEntity(TheaterRequest request) {
        return Theater.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();
    }

    public TheaterResponse toResponse(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .location(theater.getLocation())
                .build();
    }

    public void updateEntity(Theater theater, TheaterRequest request) {
        theater.setName(request.getName());
        theater.setLocation(request.getLocation());
    }
}
