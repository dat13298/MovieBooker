package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.entity.Region;
import com.datnt.moviebooker.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Region> create(@RequestBody Region region) {
        return ResponseEntity.ok(regionService.create(region));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Region> update(@PathVariable Long id, @RequestBody Region region) {
        return ResponseEntity.ok(regionService.update(id, region));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Region> getById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Region>> getAll() {
        return ResponseEntity.ok(regionService.getAll());
    }

    @GetMapping
    public ResponseEntity<Page<Region>> getPaged(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(regionService.getAll(page, size));
    }
}
