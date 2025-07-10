package com.datnt.moviebooker.dto;

public record ComboItemResponse(
        Long comboId,
        String name,
        String imageUrl,
        Integer quantity,
        Long unitPrice,
        Long totalPrice
) {}
