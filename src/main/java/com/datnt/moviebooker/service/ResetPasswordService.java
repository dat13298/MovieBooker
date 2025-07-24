package com.datnt.moviebooker.service;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.ResetPasswordRequest;
import com.datnt.moviebooker.dto.UpdateNewPasswordRequest;
import com.datnt.moviebooker.dto.ResetPasswordResponse;
import com.datnt.moviebooker.entity.OTPPassword;
import com.datnt.moviebooker.exception.BusinessException;
import com.datnt.moviebooker.repository.OTPPasswordRepository;
import com.datnt.moviebooker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final OTPPasswordRepository otpPasswordRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ApiWrapperResponse<ResetPasswordResponse> requestResetPassword(ResetPasswordRequest request) {
        try {
            var user = userService.findByEmail(request.getEmail());
            if (user == null) {
                throw new BusinessException(ResponseCode.EMAIL_NOT_MATCH);
            }

            // Create a new OTPPassword entity
            OTPPassword otpPassword = new OTPPassword();
            otpPassword.setUser(user);
            otpPassword.setOtpCode(generateOTPCode());
            otpPassword.setEmail(request.getEmail());
            otpPassword.setUsed(false);
            otpPassword.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            otpPassword.setIdRequestResetPassword(UUID.randomUUID().toString());
            otpPasswordRepository.save(otpPassword);

            // Send the OTP code via email
            try {
                String emailContent = "OTP của bạn để cập nhật mật khẩu mới là: " + otpPassword.getOtpCode();
                emailService.sendEmail(request.getEmail(), "Password Reset OTP", emailContent);
            } catch (Exception e) {
                throw new BusinessException(ResponseCode.EMAIL_SENDING_FAILED, e.getMessage());
            }

            // Prepare the response
            ResetPasswordResponse response = new ResetPasswordResponse();
            response.setIdRequestResetPassword(String.valueOf(otpPassword.getIdRequestResetPassword()));
            return ApiWrapperResponse.success(response, "Mã OTP đã được gửi thành công");

        } catch (Exception e) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public ApiWrapperResponse<String> updateNewPassword(UpdateNewPasswordRequest request) {
        try {
            var requestResetPassword = otpPasswordRepository
                    .findByRequestId(request.getIdRequestResetPassword());

            if (requestResetPassword == null) {
                throw new BusinessException(ResponseCode.PASSWORD_RESET_REQUEST_NOT_FOUND);
            }

            if (!request.getOtp().equals(requestResetPassword.getOtpCode())) {
                throw new BusinessException(ResponseCode.INVALID_OTP, "Mã OTP chính xác");
            }


            // Kiểm tra OTP đã sử dụng chưa
            if (requestResetPassword.isUsed()) {
                throw new BusinessException(ResponseCode.INVALID_OTP, "OTP đã được sử dụng");
            }

            // Kiểm tra OTP còn hạn không
            if (requestResetPassword.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new BusinessException(ResponseCode.OTP_EXPIRED);
            }

            // Update trạng thái OTP
            requestResetPassword.setUsed(true);
            otpPasswordRepository.save(requestResetPassword);

            // Cập nhật mật khẩu mới
            var user = userService.findByEmail(request.getEmail());
            if (user == null) {
                throw new BusinessException(ResponseCode.USER_NOT_FOUND, "Không tìm thấy người dùng với email: " + request.getEmail());
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ApiWrapperResponse.success("Đặt lại mật khẩu thành công");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String generateOTPCode() {
        // Generate a random 6-digit OTP code
        int randomNumber = (int) ((Math.random() * 900000) + 100000);
        return String.valueOf(randomNumber); // Generates a number between 100000 and 999999
    }
}
