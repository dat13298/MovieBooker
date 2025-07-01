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
    private String codeName;
    private String message;
    private T data;

    public static <T> ApiWrapperResponse<T> success(T data) {
        return ApiWrapperResponse.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .codeName(ResponseCode.SUCCESS.name())
                .message(ResponseCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiWrapperResponse<T> success(T data, String message) {
        return ApiWrapperResponse.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .codeName(ResponseCode.SUCCESS.name())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiWrapperResponse<T> success(ResponseCode responseCode, T data) {
        return ApiWrapperResponse.<T>builder()
                .code(responseCode.getCode())
                .codeName(responseCode.name())
                .message(responseCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiWrapperResponse<T> error(ResponseCode responseCode) {
        return ApiWrapperResponse.<T>builder()
                .code(responseCode.getCode())
                .codeName(responseCode.name())
                .message(responseCode.getMessage())
                .build();
    }

    public static <T> ApiWrapperResponse<T> error(ResponseCode responseCode, String message) {
        return ApiWrapperResponse.<T>builder()
                .code(responseCode.getCode())
                .codeName(responseCode.name())
                .message(message)
                .build();
    }

    public static <T> ApiWrapperResponse<T> error(int code, String message) {
        return ApiWrapperResponse.<T>builder()
                .code(code)
                .codeName(ResponseCode.INTERNAL_SERVER_ERROR.name())
                .message(message)
                .build();
    }
}
