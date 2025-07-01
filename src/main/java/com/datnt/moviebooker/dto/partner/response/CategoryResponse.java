package com.datnt.moviebooker.dto.partner.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryResponse {
    @JsonProperty("categoryId")
    private int categoryId;

    @JsonProperty("categoryNm")
    private String categoryName;

    @JsonProperty("categoryImg")
    private String categoryImg;

}
