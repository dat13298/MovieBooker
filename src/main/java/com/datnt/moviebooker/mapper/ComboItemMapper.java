package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.ComboItemRequest;
import com.datnt.moviebooker.dto.ComboItemResponse;
import com.datnt.moviebooker.entity.Booking;
import com.datnt.moviebooker.entity.ComboFood;
import com.datnt.moviebooker.entity.FoodBooking;
import org.springframework.stereotype.Component;

@Component
public class ComboItemMapper {

    public ComboItemResponse toResponse(FoodBooking foodBooking) {
        ComboFood combo = foodBooking.getComboFood();
        return new ComboItemResponse(
                combo.getId(),
                combo.getName(),
                combo.getImageUrl(),
                foodBooking.getQuantity(),
                combo.getPrice(),
                combo.getPrice() * foodBooking.getQuantity()
        );
    }

    public FoodBooking toEntity(ComboItemRequest request, ComboFood combo, Booking booking) {
        return FoodBooking.builder()
                .comboFood(combo)
                .booking(booking)
                .quantity(request.quantity())
                .price(combo.getPrice())
                .build();
    }
}
