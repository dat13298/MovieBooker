package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.TheaterRequest;
import com.datnt.moviebooker.dto.TheaterResponse;
import com.datnt.moviebooker.service.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @GetMapping
    public ResponseEntity<Page<TheaterResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(theaterService.getAllTheaters(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterResponse> findById(@PathVariable("id") Long id) {
        return theaterService.getTheaterById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TheaterResponse> create(@Valid @RequestBody TheaterRequest request) {
        return ResponseEntity.ok(theaterService.createTheater(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TheaterResponse> update(@PathVariable("id") Long id, @Valid @RequestBody TheaterRequest request) {
        return ResponseEntity.ok(theaterService.updateTheater(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<TheaterResponse> delete(@PathVariable("id") Long id) {
        theaterService.deleteTheater(id);
        return ResponseEntity.noContent().build();
    }
}
