package com.datnt.moviebooker.service.partner;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.GotitClient;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.partner.response.ApiWrapper;
import com.datnt.moviebooker.dto.partner.response.BrandResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class BrandService {
    private final GotitClient gotitClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public BrandService(GotitClient gotitClient, ObjectMapper objectMapper) {
        this.gotitClient = gotitClient;
        this.objectMapper = objectMapper;
    }

    public ApiWrapperResponse<List<BrandResponse>> getAllBrands() {
        try {
            HttpResponse<String> response = gotitClient.callApi(
                    "GET",
                    GotitClient.PATH_GET_ALL_BRANDS,
                    null,
                    null
            );
            if (response.statusCode() == 200) {
                ApiWrapper<List<BrandResponse>> apiWrapper = objectMapper.readValue(
                        response.body(),
                        objectMapper.getTypeFactory().constructParametricType(ApiWrapper.class, objectMapper.getTypeFactory().constructCollectionType(List.class, BrandResponse.class))
                );
                return ApiWrapperResponse.success(ResponseCode.SUCCESS, apiWrapper.getData());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return ApiWrapperResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
