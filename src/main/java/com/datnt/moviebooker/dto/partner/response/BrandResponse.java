package com.datnt.moviebooker.dto.partner.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BrandResponse {
    @JsonProperty("brandId")
    private int brandId;

    @JsonProperty("brandNm")
    private String brandName;

    @JsonProperty("brandLogo")
    private String brandLogo;

    @JsonProperty("shortDesc")
    private String shortDesc;

    @JsonProperty("description")
    private String description;

    @JsonProperty("categoryID")
    private List<Integer> categoryID;

    @JsonProperty("usageMethods")
    private List<UsageMethod> usageMethods;

    @Data
    public static class UsageMethod {
        @JsonProperty("type")
        private String type;
        @JsonProperty("title")
        private String title;
        @JsonProperty("description")
        private String description;
        @JsonProperty("order")
        private int order;
    }
}
