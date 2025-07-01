package com.datnt.moviebooker.dto.partner.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GiftDetailResponse {
    @JsonProperty("productId")
    private int productId;
    @JsonProperty("productNm")
    private String productNm;
    @JsonProperty("productImg")
    private String productImg;
    @JsonProperty("productSubImg")
    private List<String> productSubImg;
    @JsonProperty("brandId")
    private int brandId;
    @JsonProperty("brandNm")
    private String brandNm;
    @JsonProperty("productType")
    private String productType;
    @JsonProperty("brandNameSlug")
    private String brandNameSlug;
    @JsonProperty("brandPhone")
    private String brandPhone;
    @JsonProperty("brandAddress")
    private String brandAddress;
    @JsonProperty("brandDesc")
    private String brandDesc;
    @JsonProperty("brandServiceGuide")
    private String brandServiceGuide;
    @JsonProperty("serviceGuide")
    private String serviceGuide;
    @JsonProperty("brandLogo")
    private String brandLogo;
    @JsonProperty("link")
    private String link;
    @JsonProperty("prices")
    private List<Price> prices;
    @JsonProperty("nameSlug")
    private String nameSlug;
    @JsonProperty("productDesc")
    private String productDesc;
    @JsonProperty("productShortDesc")
    private String productShortDesc;
    @JsonProperty("terms")
    private String terms;
    @JsonProperty("categoryId")
    private int categoryId;
    @JsonProperty("categoryNm")
    private String categoryNm;
    @JsonProperty("categoryImg")
    private String categoryImg;
    @JsonProperty("brandRedeem")
    private List<BrandRedeem> brandRedeem;
    @JsonProperty("storeList")
    private List<Object> storeList;

    @Data
    public static class Price {
        @JsonProperty("priceId")
        private int priceId;
        @JsonProperty("priceNm")
        private String priceNm;
        @JsonProperty("priceValue")
        private int priceValue;
    }

    @Data
    public static class BrandRedeem {
        @JsonProperty("brandID")
        private int brandID;
        @JsonProperty("brandName")
        private String brandName;
        @JsonProperty("brandLogo")
        private String brandLogo;
        @JsonProperty("brandNameSlug")
        private String brandNameSlug;
        @JsonProperty("brandPhone")
        private String brandPhone;
        @JsonProperty("brandAddress")
        private String brandAddress;
        @JsonProperty("brandDesc")
        private String brandDesc;
        @JsonProperty("brandServiceGuide")
        private String brandServiceGuide;
        @JsonProperty("categoryId")
        private int categoryId;
        @JsonProperty("categoryNm")
        private String categoryNm;
        @JsonProperty("categoryImg")
        private String categoryImg;
    }
}
