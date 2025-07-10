package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.ComboFoodRequest;
import com.datnt.moviebooker.dto.ComboFoodResponse;
import com.datnt.moviebooker.entity.ComboFood;
import org.springframework.stereotype.Component;

@Component
public class ComboFoodMapper {

    public ComboFood toEntity(ComboFoodRequest request) {
        return ComboFood.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .isActive(request.getIsActive())
                .foodType(request.getFoodType())
                .imageUrl(null)
                .build();
    }

    public ComboFoodResponse toResponse(ComboFood combo) {
        return new ComboFoodResponse(
                combo.getId(),
                combo.getName(),
                combo.getDescription(),
                combo.getPrice(),
                combo.getImageUrl(),
                combo.getIsActive(),
                combo.getFoodType()
        );
    }

    public void updateEntity(ComboFood combo, ComboFoodRequest request) {
        combo.setName(request.getName());
        combo.setDescription(request.getDescription());
        combo.setPrice(request.getPrice());
        combo.setIsActive(request.getIsActive());
        combo.setFoodType(request.getFoodType());
    }
}
