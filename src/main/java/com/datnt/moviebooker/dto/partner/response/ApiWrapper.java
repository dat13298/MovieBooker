package com.datnt.moviebooker.dto.partner.response;

import lombok.Data;

@Data
public class ApiWrapper<T> {
    private String status;
    private int statusCode;
    private String error;
    private String message;
    private T data;
}
