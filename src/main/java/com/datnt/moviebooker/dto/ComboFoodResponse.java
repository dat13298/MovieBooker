package com.datnt.moviebooker.dto;

public record ComboFoodResponse(
        Long id,
        String name,
        String description,
        Long price,
        String imageUrl,
        Boolean isActive,
        String foodType
) {}
