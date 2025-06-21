package com.datnt.moviebooker.exception;

import com.datnt.moviebooker.common.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ResponseCode responseCode;
    private final String message;

    public BusinessException(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.message = responseCode.getMessage();
    }

    public BusinessException(ResponseCode responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }
}
