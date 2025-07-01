package com.datnt.moviebooker.service.partner;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.GotitClient;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.partner.response.GiftListResponse;
import com.datnt.moviebooker.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GiftListService {
    private final GotitClient gotitClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public GiftListService(GotitClient gotitClient, ObjectMapper objectMapper) {
        this.gotitClient = gotitClient;
        this.objectMapper = objectMapper;
    }

    public ApiWrapperResponse<GiftListResponse> getGiftList(Integer categoryId, Integer brandId) {
        String path = GotitClient.PATH_GET_GIFT_LIST
                .replace("{categoryId}", String.valueOf(categoryId != null ? categoryId : ""))
                .replace("{brandId}", String.valueOf(brandId != null ? brandId : ""))
                + "&page=1&pageSize=20&isExcludeStoreListInfo=false&storeListPage=1&storeListPageSize=2";

        String requestBody = "{"
                + "\"minPrice\": 1,"
                + "\"maxPrice\": 10000000,"
                + "\"orderBy\": \"asc\","
                + "\"pagination\": {"
                + "    \"pageSize\": 500,"
                + "    \"page\": 1"
                + "}"
                + "}";

        try {
            HttpResponse<String> response = gotitClient.callApi(
                    "GET",
                    path,
                    null,
                    requestBody
            );

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode dataNode = root.get("data");

                GiftListResponse giftListResponse = new GiftListResponse();

                if (dataNode != null && dataNode.isArray() && !dataNode.isEmpty()) {
                    // Lấy phần tử đầu tiên của mảng data
                    JsonNode firstDataElement = dataNode.get(0);
                    JsonNode productListNode = firstDataElement.get("productList");

                    if (productListNode != null && productListNode.isArray()) {
                        List<GiftListResponse.Product> products = objectMapper.readValue(
                                productListNode.toString(),
                                objectMapper.getTypeFactory().constructCollectionType(
                                        List.class,
                                        GiftListResponse.Product.class
                                )
                        );
                        giftListResponse.setProductList(products);
                        log.info("Successfully mapped {} products", products.size());
                    } else {
                        log.warn("productList node not found or not an array");
                        giftListResponse.setProductList(new ArrayList<>());
                    }
                } else {
                    log.warn("Data node is null, not an array, or empty");
                    giftListResponse.setProductList(new ArrayList<>());
                }

                return ApiWrapperResponse.<GiftListResponse>builder()
                        .data(giftListResponse)
                        .build();
            } else {
                log.error("API returned status code: {}", response.statusCode());
                throw new BusinessException(ResponseCode.NOT_FOUND, "No gifts found for the given category or brand");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error fetching gift list: ", e);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, "Error fetching gift list: " + e.getMessage());
        }
    }
}