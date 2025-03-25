package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.ScreenRequest;
import com.datnt.moviebooker.dto.ScreenResponse;
import com.datnt.moviebooker.entity.Screen;
import org.springframework.stereotype.Component;

@Component
public class ScreenMapper {

    public Screen toEntity(ScreenRequest request) {
        return Screen.builder()
                .name(request.getName())
                .theater(null)
                .build();
    }

    public ScreenResponse toResponse(Screen screen) {
        return ScreenResponse.builder()
                .id(screen.getId())
                .name(screen.getName())
                .theaterId(screen.getTheater().getId())
                .build();
    }
}
