package com.datnt.moviebooker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAllVoucherResponse {
    private Long id;
    private String evoucherName;
    private String brandName;
    private String expiryDate;
    private String imgUrl;
}
