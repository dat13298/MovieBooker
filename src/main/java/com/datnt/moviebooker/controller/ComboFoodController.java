package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.dto.ComboFoodRequest;
import com.datnt.moviebooker.dto.ComboFoodResponse;
import com.datnt.moviebooker.service.ComboFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/combos")
@RequiredArgsConstructor
public class ComboFoodController {

    private final ComboFoodService comboFoodService;

    @GetMapping
    public ResponseEntity<List<ComboFoodResponse>> getAllActiveCombos() {
        return ResponseEntity.ok(comboFoodService.getAllActiveCombos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComboFoodResponse> getComboById(@PathVariable Long id) {
        return ResponseEntity.ok(comboFoodService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComboFoodResponse> createCombo(
            @RequestPart("data") ComboFoodRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        request.setImage(image);
        return ResponseEntity.ok(comboFoodService.createCombo(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComboFoodResponse> updateCombo(
            @PathVariable Long id,
            @RequestPart("data") ComboFoodRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        request.setImage(image);
        return ResponseEntity.ok(comboFoodService.updateCombo(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCombo(@PathVariable Long id) {
        comboFoodService.deleteCombo(id);
        return ResponseEntity.noContent().build();
    }
}
