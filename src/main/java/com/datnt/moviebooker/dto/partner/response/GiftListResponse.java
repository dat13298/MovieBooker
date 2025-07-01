package com.datnt.moviebooker.dto.partner.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public class GiftListResponse {
    @JsonProperty("productList")
    private List<Product> productList;

    @Data
    public static class Product {
        @JsonProperty("productId")
        private int productId;
        @JsonProperty("productNm")
        private String productName;
        @JsonProperty("productImg")
        private String productImg;
        @JsonProperty("productDesc")
        private String productDesc;
        @JsonProperty("productShortDesc")
        private String productShortDesc;
        @JsonProperty("brandId")
        private int brandId;
        @JsonProperty("brandNm")
        private String brandName;
        @JsonProperty("brandLogo")
        private String brandLogo;
        @JsonProperty("brandServiceGuide")
        private String brandServiceGuide;
        @JsonProperty("categoryId")
        private int categoryId;
        @JsonProperty("categoryNm")
        private String categoryName;
        @JsonProperty("categoryImg")
        private String categoryImg;
        @JsonProperty("productType")
        private String productType;
        @JsonProperty("prices")
        private List<Price> prices;
        @JsonProperty("link")
        private String link;
        @JsonProperty("storeList")
        private List<Store> storeList;
        @JsonProperty("totalStore")
        private int totalStore;
    }

    @Data
    public static class Price {
        @JsonProperty("priceId")
        private int priceId;
        @JsonProperty("priceNm")
        private String priceNm;
        @Setter
        private long priceValue;

        @JsonProperty("priceValue")
        public long getPriceValue() {
            return priceValue / 1000;
        }

    }

    @Data
    public static class Store {
        @JsonProperty("storeId")
        private int storeId;
        @JsonProperty("storeNm")
        private String storeNm;
        @JsonProperty("storeAddr")
        private String storeAddr;
        @JsonProperty("lat")
        private double lat;
        @JsonProperty("long")
        private double lng;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("city_id")
        private int cityId;
        @JsonProperty("city")
        private String city;
        @JsonProperty("dist_id")
        private int distId;
        @JsonProperty("district")
        private String district;
    }
}
