package com.datnt.moviebooker.service.partner;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.GotitClient;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.partner.response.GiftListResponse;
import com.datnt.moviebooker.dto.partner.response.ApiWrapper;
import com.datnt.moviebooker.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class GiftDetailService {
    private static final Logger log = LoggerFactory.getLogger(GiftDetailService.class);
    private final GotitClient gotitClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public GiftDetailService(GotitClient gotitClient, ObjectMapper objectMapper) {
        this.gotitClient = gotitClient;
        this.objectMapper = objectMapper;
    }

    public ApiWrapperResponse<List<GiftListResponse.Product>> getGiftDetail(int productId) {
        String path = GotitClient.PATH_GET_GIFT_DETAIL.replace("{productId}", String.valueOf(productId));
        try {
            HttpResponse<String> response = gotitClient.callApi(
                    "GET",
                    path,
                    null,
                    null
            );
            if (response.statusCode() == 200) {
                ApiWrapper<List<GiftListResponse.Product>> apiWrapper = objectMapper.readValue(
                        response.body(),
                        objectMapper.getTypeFactory().constructParametricType(ApiWrapper.class,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, GiftListResponse.Product.class))
                );
                return ApiWrapperResponse.success(ResponseCode.SUCCESS, apiWrapper.getData());
            } else {
                throw new BusinessException(ResponseCode.NOT_FOUND, "Gift not found for the given product ID");
            }
        } catch (IOException | InterruptedException e) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, "Error fetching gift detail: " + e.getMessage());
        }
    }
}
