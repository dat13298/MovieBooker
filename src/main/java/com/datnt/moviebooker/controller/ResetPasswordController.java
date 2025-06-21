package com.datnt.moviebooker.controller;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.dto.ResetPasswordRequest;
import com.datnt.moviebooker.dto.UpdateNewPasswordRequest;
import com.datnt.moviebooker.dto.ResetPasswordResponse;
import com.datnt.moviebooker.service.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reset-password")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @PostMapping("/request-reset-password")
    public ResponseEntity<ApiWrapperResponse<ResetPasswordResponse>> requestResetPassword(
            @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(resetPasswordService.requestResetPassword(request));
    }

    @PostMapping("/update-new-password")
    public ResponseEntity<ApiWrapperResponse<String>> updateNewPassword(
            @RequestBody UpdateNewPasswordRequest request) {
        return ResponseEntity.ok(resetPasswordService.updateNewPassword(request));
    }
}
