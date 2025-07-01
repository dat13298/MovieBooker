package com.datnt.moviebooker.exception;

import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.common.ApiWrapperResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
//        HttpStatus httpStatus;
//
//        int code = ex.getResponseCode().getCode();
//        if (code >= 400 && code < 500) {
//            httpStatus = HttpStatus.BAD_REQUEST;
//        } else if (code >= 500) {
//            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//        } else {
//            httpStatus = HttpStatus.OK;
//        }
//
//        String message = ex.getResponseCode().getMessage();
//
//        return ResponseEntity
//                .status(httpStatus)
//                .body(new ErrorResponse(code, message));
//    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiWrapperResponse<Object>> handleBusinessException(BusinessException ex) {
        ApiWrapperResponse<Object> response = ApiWrapperResponse.builder()
                .code(ex.getResponseCode().getCode())
                .codeName(ex.getResponseCode().getName())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        ResponseCode.INTERNAL_SERVER_ERROR.getCode(),
                        "Lỗi hệ thống: " + ex.getMessage()));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private int code;
        private String message;
    }
}
