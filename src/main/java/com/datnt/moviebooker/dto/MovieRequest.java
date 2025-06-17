package com.datnt.moviebooker.dto;

import com.datnt.moviebooker.constant.MovieStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Data
public class MovieRequest {
    @NotBlank @Size(max = 255)
    private String title;

    @NotBlank
    private String description;

    @Min(1)
    private int duration;

    @NotBlank
    private String rating;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @NotBlank
    private String director;

    @NotBlank
    private String actors;

    @NotBlank
    private String movieType;

    @NotBlank
    private String language;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate premiereDate;

    @NotNull
    private MovieStatus movieStatus;

    @NotBlank
    private String screenType;

    @NotNull
    private MultipartFile image;
}
