package com.datnt.moviebooker.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ComboFoodRequest {
    private String name;
    private String description;
    private Long price;
    private Boolean isActive;
    private String foodType;
    private MultipartFile image;

}
