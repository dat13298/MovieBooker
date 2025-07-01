package com.datnt.moviebooker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetVoucherUseResponse {
    private String code;
    private String serial;
    private String expiryDate;
}
