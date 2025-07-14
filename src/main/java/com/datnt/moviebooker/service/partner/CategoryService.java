package com.datnt.moviebooker.service.partner;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.GotitClient;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.partner.response.ApiWrapper;
import com.datnt.moviebooker.dto.partner.response.CategoryResponse;
import com.datnt.moviebooker.exception.BusinessException;
import com.datnt.moviebooker.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryService {
    private final GotitClient gotitClient;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Value("${cache.category.expiration:2}") // Default cache expiration is 2 hours
    private long cacheExpirationHours;

    @Autowired
    public CategoryService(GotitClient gotitClient, ObjectMapper objectMapper, RedisService redisService) {
        this.gotitClient = gotitClient;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
    }

    public ApiWrapperResponse<List<CategoryResponse>> getAllCategories() {
        String cacheKey = "categories:all";

        // Check cache first
        List<CategoryResponse> cachedCategories = redisService.getDataFromJson(
                cacheKey,
                new TypeReference<List<CategoryResponse>>() {}
        );
        if (cachedCategories != null) {
            return ApiWrapperResponse.success(ResponseCode.SUCCESS, cachedCategories);
        }

        // If not in cache, call API
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

                List<CategoryResponse> categories = apiWrapper.getData();

                // Save to cache
                redisService.saveDataGeneric(cacheKey, categories, cacheExpirationHours, TimeUnit.HOURS);

                return ApiWrapperResponse.success(ResponseCode.SUCCESS, categories);
            }
        } catch (IOException | InterruptedException e) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }

        return ApiWrapperResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
