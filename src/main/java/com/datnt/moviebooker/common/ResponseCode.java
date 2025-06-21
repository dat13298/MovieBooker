package com.datnt.moviebooker.common;

public enum ResponseCode {
    // Success responses (2xx)
    SUCCESS(200, "Thành công"),
    CREATED(201, "Tạo mới thành công"),
    ACCEPTED(202, "Yêu cầu được chấp nhận"),

    // Client errors (4xx)
    BAD_REQUEST(400, "Yêu cầu không hợp lệ"),
    UNAUTHORIZED(401, "Chưa xác thực"),
    FORBIDDEN(403, "Không có quyền truy cập"),
    NOT_FOUND(404, "Không tìm thấy tài nguyên"),
    METHOD_NOT_ALLOWED(405, "Phương thức không được phép"),
    CONFLICT(409, "Xung đột dữ liệu"),
    INVALID_INPUT(422, "Dữ liệu đầu vào không hợp lệ"),

    // Server errors (5xx)
    INTERNAL_SERVER_ERROR(500, "Lỗi hệ thống"),
    SERVICE_UNAVAILABLE(503, "Dịch vụ không khả dụng"),

    // Custom business codes
    TOKEN_EXPIRED(4001, "Token đã hết hạn"),
    INVALID_TOKEN(4002, "Token không hợp lệ"),
    USER_NOT_FOUND(4003, "Không tìm thấy người dùng"),
    INVALID_CREDENTIALS(4004, "Thông tin đăng nhập không chính xác"),
    EMAIL_ALREADY_EXISTS(4005, "Email đã tồn tại"),
    INVALID_OTP(4006, "Mã OTP không hợp lệ"),
    OTP_EXPIRED(4007, "Mã OTP đã hết hạn"),
    PAYMENT_FAILED(4008, "Thanh toán thất bại"),
    SEAT_ALREADY_BOOKED(4009, "Ghế đã được đặt"),
    EMAIL_NOT_MATCH(4010, "Email không khớp với người dùng hiện tại"),
    EMAIL_SENDING_FAILED(4011, "Không thể gửi email"),
    PASSWORD_RESET_REQUEST_NOT_FOUND(4012, "Không tìm thấy yêu cầu đặt lại mật khẩu");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
