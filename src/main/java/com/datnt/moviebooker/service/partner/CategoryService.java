package com.datnt.moviebooker.service.partner;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.GotitClient;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.constant.Gender;
import com.datnt.moviebooker.dto.partner.response.ApiWrapper;
import com.datnt.moviebooker.dto.partner.response.CategoryResponse;
import com.datnt.moviebooker.exception.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

@Service
public class CategoryService {
    private final GotitClient gotitClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public CategoryService(GotitClient gotitClient, ObjectMapper objectMapper) {
        this.gotitClient = gotitClient;
        this.objectMapper = objectMapper;
    }

    public ApiWrapperResponse<List<CategoryResponse>> getAllCategories() {
        try {
            HttpResponse<String> response = gotitClient.callApi(
                    "GET",
                    GotitClient.PATH_GET_ALL_CATEGORIES,
                    null,
                    null
            );
            if (response.statusCode() == 200) {
                ApiWrapper<List<CategoryResponse>> apiWrapper = objectMapper.readValue(
                        response.body(),
                        objectMapper.getTypeFactory().constructParametricType(ApiWrapper.class, objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryResponse.class))
                );
                return ApiWrapperResponse.success(ResponseCode.SUCCESS, apiWrapper.getData());
            }
        } catch (IOException | InterruptedException e) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return ApiWrapperResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
