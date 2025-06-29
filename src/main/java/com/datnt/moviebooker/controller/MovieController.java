package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.MovieRequest;
import com.datnt.moviebooker.dto.MovieResponse;
import com.datnt.moviebooker.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Page<MovieResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> findById(@PathVariable("id") Long id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Validated(MovieRequest.Create.class)
    public ResponseEntity<MovieResponse> createMovie(@Valid @ModelAttribute MovieRequest movieRequest) {
        return ResponseEntity.ok(movieService.createMovie(movieRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Validated(MovieRequest.Update.class)
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, @Valid @ModelAttribute MovieRequest movieRequest) {
        return ResponseEntity.ok(movieService.updateMovie(id, movieRequest));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
