package com.datnt.moviebooker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class MovieRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int duration;

    @NotNull(message = "Rating is required")
    private String rating;

    @Schema(type = "string", format = "date", example = "2025-06-10")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Release Date is required")
    private Date releaseDate;


    @NotNull(message = "Image is required")
    private MultipartFile image;
}
