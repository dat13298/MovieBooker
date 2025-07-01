package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.dto.partner.request.RedeemVoucherRequest;
import com.datnt.moviebooker.dto.partner.response.BrandResponse;
import com.datnt.moviebooker.dto.partner.response.CategoryResponse;
import com.datnt.moviebooker.dto.partner.response.GiftListResponse;
import com.datnt.moviebooker.service.partner.BrandByCategoryService;
import com.datnt.moviebooker.service.partner.BrandService;
import com.datnt.moviebooker.service.partner.CategoryService;
import com.datnt.moviebooker.service.partner.GiftDetailService;
import com.datnt.moviebooker.service.partner.GiftListService;
import com.datnt.moviebooker.service.partner.RedeemVoucherService;
import lombok.AllArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/partner")
@AllArgsConstructor
public class PartnerController {
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final BrandByCategoryService brandByCategoryService;
    private final GiftListService giftListService;
    private final GiftDetailService giftDetailService;
    private final RedeemVoucherService redeemVoucherService;

    /**
     * Lấy danh sách category từ hệ thống Got It.
     *
     * @return ApiWrapperResponse chứa danh sách category
     */
    @GetMapping("/categories")
    public ApiWrapperResponse<List<CategoryResponse>> getCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * Lấy danh sách brand từ hệ thống Got It.
     *
     * @return ApiWrapperResponse chứa danh sách brand
     */
    @GetMapping("/brands")
    public ApiWrapperResponse<List<BrandResponse>> getBrands() {
        return brandService.getAllBrands();
    }

    /**
     * Lấy danh sách brand theo categoryId từ hệ thống Got It.
     *
     * @param categoryId category cần lấy brand
     * @return ApiWrapperResponse chứa danh sách brand
     */
    @GetMapping("/brands/by-category")
    public ApiWrapperResponse<List<BrandResponse>> getBrandsByCategory(@RequestParam int categoryId) {
        return brandByCategoryService.getBrandsByCategoryId(categoryId);
    }

    /**
     * Lấy danh sách voucher (gift list) theo categoryId và brandId từ hệ thống Got It.
     *
     * @param categoryId category cần lấy voucher (có thể null)
     * @param brandId brand cần lấy voucher (có thể null)
     * @return ApiWrapperResponse chứa danh sách voucher
     */
    @GetMapping("/gifts")
    public ApiWrapperResponse<GiftListResponse> getGiftList(@RequestParam(required = false) Integer categoryId,
                                                                         @RequestParam(required = false) Integer brandId) {
        return giftListService.getGiftList(categoryId, brandId);
    }

    /**
     * Lấy chi tiết voucher (gift) theo productId từ hệ thống Got It.
     *
     * @param productId id của voucher cần lấy chi tiết
     * @return ApiWrapperResponse chứa chi tiết voucher
     */
    @GetMapping("/gift-detail")
    public ApiWrapperResponse<GiftListResponse.Product> getGiftDetail(@RequestParam int productId) {
        return giftDetailService.getGiftDetail(productId);
    }

    /**
     * Đổi voucher từ hệ thống Got It.
     *
     * @param request Thông tin request đổi voucher
     * @return ApiWrapperResponse chứa thông báo kết quả đổi voucher
     */
    @PostMapping("/redeem-voucher")
    public ApiWrapperResponse<String> redeemVoucher(@Validated @RequestBody RedeemVoucherRequest request) {
        return redeemVoucherService.handlerRedeemVoucher(request);
    }
}
