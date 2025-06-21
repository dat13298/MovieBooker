package com.datnt.moviebooker.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiWrapperResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiWrapperResponse<T> success(T data) {
        return ApiWrapperResponse.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiWrapperResponse<T> success(T data, String message) {
        return ApiWrapperResponse.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiWrapperResponse<T> error(ResponseCode responseCode) {
        return ApiWrapperResponse.<T>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .build();
    }

    public static <T> ApiWrapperResponse<T> error(ResponseCode responseCode, String message) {
        return ApiWrapperResponse.<T>builder()
                .code(responseCode.getCode())
                .message(message)
                .build();
    }

    public static <T> ApiWrapperResponse<T> error(int code, String message) {
        return ApiWrapperResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
