package com.datnt.moviebooker.dto.partner.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RedeemVoucherRequest {
    @NotNull(message = "ID voucher không được để trống")
    @Min(value = 1, message = "ID voucher phải lớn hơn 0")
    private int giftId;

    @NotNull(message = "ID giá không được để trống")
    @Min(value = 1, message = "ID giá phải lớn hơn 0")
    private int priceId;

    @NotNull(message = "Giá trị voucher không được để trống")
    @Min(value = 0, message = "Giá trị voucher không được âm")
    private int priceValue;
}
