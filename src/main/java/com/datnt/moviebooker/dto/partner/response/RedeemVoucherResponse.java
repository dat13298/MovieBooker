package com.datnt.moviebooker.dto.partner.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class RedeemVoucherResponse {
    @JsonProperty("orderName")
    private String orderName;
    @JsonProperty("vouchers")
    private List<Voucher> vouchers;

    @Data
    public static class Voucher {
        @JsonProperty("transactionRefId")
        private String transactionRefId;
        @JsonProperty("voucherCode")
        private String voucherCode;
        @JsonProperty("voucherLink")
        private String voucherLink;
        @JsonProperty("voucherLinkCode")
        private String voucherLinkCode;
        @JsonProperty("voucherImageLink")
        private String voucherImageLink;
        @JsonProperty("voucherCoverLink")
        private String voucherCoverLink;
        @JsonProperty("voucherCoverLinkCode")
        private String voucherCoverLinkCode;
        @JsonProperty("voucherSerial")
        private String voucherSerial;
        @JsonProperty("expiryDate")
        private String expiryDate;
        @JsonProperty("product")
        private Product product;
    }

    @Data
    public static class Product {
        @JsonProperty("productId")
        private int productId;
        @JsonProperty("productNm")
        private String productNm;
        @JsonProperty("productImg")
        private String productImg;
        @JsonProperty("brandId")
        private int brandId;
        @JsonProperty("brandNm")
        private String brandNm;
        @JsonProperty("brandServiceGuide")
        private String brandServiceGuide;
        @JsonProperty("link")
        private String link;
        @JsonProperty("price")
        private Price price;
        @JsonProperty("productDesc")
        private String productDesc;
        @JsonProperty("terms")
        private String terms;
        @JsonProperty("productType")
        private String productType;
    }

    @Data
    public static class Price {
        @JsonProperty("priceId")
        private int priceId;
        @JsonProperty("priceNm")
        private String priceNm;
        @JsonProperty("priceValue")
        private int priceValue;
    }
}
