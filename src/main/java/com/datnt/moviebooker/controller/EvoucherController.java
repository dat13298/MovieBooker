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

    /**
     * Lấy danh sách voucher UNUSED của người dùng hiện tại
     * @return Danh sách voucher UNUSED được bọc trong ApiWrapperResponse
     */
    @GetMapping("/unused")
    @Operation(summary = "Get UNUSED vouchers for current user",
            description = "Retrieves UNUSED vouchers associated with the authenticated user")
    public ApiWrapperResponse<List<GetAllVoucherResponse>> getUnusedVouchers() {
        return evoucherService.getUnusedVouchers();
    }

    /**
     * Lấy danh sách voucher có trạng thái khác UNUSED của người dùng hiện tại
     * @return Danh sách voucher khác UNUSED được bọc trong ApiWrapperResponse
     */
    @GetMapping("/others")
    @Operation(summary = "Get other vouchers for current user",
            description = "Retrieves vouchers with statuses other than UNUSED associated with the authenticated user")
    public ApiWrapperResponse<List<GetAllVoucherResponse>> getOtherVouchers() {
        return evoucherService.getOtherVouchers();
    }
}
