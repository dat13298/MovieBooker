package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.dto.GetAllVoucherResponse;
import com.datnt.moviebooker.dto.GetVoucherUseResponse;
import com.datnt.moviebooker.service.EvoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evouchers")
@RequiredArgsConstructor
@Tag(name = "Evoucher Controller", description = "API endpoints for managing vouchers")
public class EvoucherController {

    private final EvoucherService evoucherService;

    /**
     * Lấy danh sách voucher của người dùng hiện tại
     * @return Danh sách voucher được bọc trong ApiWrapperResponse
     */
    @GetMapping
    @Operation(summary = "Get all vouchers for current user",
            description = "Retrieves all vouchers associated with the authenticated user")
    public ApiWrapperResponse<List<GetAllVoucherResponse>> getAllVouchers() {
        return evoucherService.getAllVouchers();
    }

    /**
     * Sử dụng voucher theo ID
     * @param evoucherId ID của voucher cần sử dụng
     * @return Thông tin voucher sau khi sử dụng được bọc trong ApiWrapperResponse
     */
    @PostMapping("/{evoucherId}/use")
    @Operation(summary = "Use a voucher",
            description = "Marks a voucher as used if it's valid and not expired")
    public ApiWrapperResponse<GetVoucherUseResponse> useVoucher(
            @PathVariable Long evoucherId) {
        return evoucherService.useVoucher(evoucherId);
    }
}
