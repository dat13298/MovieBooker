package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.dto.GetPointResponse;
import com.datnt.moviebooker.service.AuthService;
import com.datnt.moviebooker.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
@Tag(name = "Point", description = "Point management APIs")
public class PointController {
    private final PointService pointService;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "Get current user's point information")
    public ApiWrapperResponse<GetPointResponse> getPoint() {
        return pointService.getPoint(authService.getCurrentUserId());
    }
}
