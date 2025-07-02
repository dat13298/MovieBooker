package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.SeatResponse;
import com.datnt.moviebooker.dto.ShowTimeRequest;
import com.datnt.moviebooker.dto.ShowTimeResponse;
import com.datnt.moviebooker.service.SeatService;
import com.datnt.moviebooker.service.ShowTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/show-times")
@RequiredArgsConstructor
public class ShowTimeController {

    private final ShowTimeService showTimeService;
    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<Page<ShowTimeResponse>> getAllShowTimes(Pageable pageable) {
        return ResponseEntity.ok(showTimeService.getAllShowTimes(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowTimeResponse> getShowTimeById(@PathVariable Long id) {
        return ResponseEntity.ok(showTimeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowTimeResponse> createShowTime(@Valid @RequestBody ShowTimeRequest request) {
        return ResponseEntity.ok(showTimeService.createShowTime(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowTimeResponse> updateShowTime(@PathVariable Long id, @Valid @RequestBody ShowTimeRequest request) {
        return ResponseEntity.ok(showTimeService.updateShowTime(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShowTime(@PathVariable Long id) {
        showTimeService.deleteShowTime(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponse>> getSeatsOfShowTime(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.findSeatsByShowTime(id));
    }

}
