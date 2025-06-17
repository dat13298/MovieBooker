package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.TheaterRequest;
import com.datnt.moviebooker.dto.TheaterResponse;
import com.datnt.moviebooker.entity.Region;
import com.datnt.moviebooker.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public Theater toEntity(TheaterRequest request, Region region) {
        return Theater.builder()
                .name(request.getName())
                .address(request.getAddress()) // đổi location → address
                .region(region)                // bổ sung region
                .build();
    }

    public TheaterResponse toResponse(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .regionName(theater.getRegion().getName())
                .build();
    }

    public void updateEntity(Theater theater, TheaterRequest request) {
        theater.setName(request.getName());
        theater.setAddress(request.getAddress());
    }
}
