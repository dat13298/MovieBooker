package com.datnt.moviebooker.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class GotitClient {
    private static final String BASE_URL = "https://api-biz-stg.gotit.vn/api/v4.0";
    private final HttpClient httpClient;
    private final String authorizationKey;

    public static final String PATH_GET_ALL_CATEGORIES = "/categories";
    public static final String PATH_GET_ALL_BRANDS = "/brands";
    public static final String PATH_GET_BRAND_BY_CATEGORY = "/categories/{categoryId}/brands";
    public static final String PATH_GET_GIFT_LIST = "/products?categoryId={categoryId}&brandId={brandId}";
    public static final String PATH_GET_GIFT_DETAIL = "/products/{productId}";
    public static final String PATH_REDEEM_VOUCHER = "/vouchers/v";
    public static final String PREFIX_REDEEM_VOUCHER = "000578_";

    public GotitClient(@Value("${gotit.authorization-key}") String authorizationKey) {
        this.httpClient = HttpClient.newHttpClient();
        this.authorizationKey = authorizationKey;
    }

    /**
     * Generic method to call Gotit API
     * @param method HTTP method (GET, POST, PUT, DELETE, ...)
     * @param path API endpoint path (e.g. "/orders")
     * @param headers Map of headers
     * @param body Request body (for POST/PUT), can be null
     * @return HttpResponse<String> response from Gotit
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> callApi(String method, String path, Map<String, String> headers, String body)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path));

        // Set method and body
        if (method.equalsIgnoreCase("GET")) {
            builder.GET();
        } else if (method.equalsIgnoreCase("POST")) {
            builder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
        } else if (method.equalsIgnoreCase("PUT")) {
            builder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
        } else if (method.equalsIgnoreCase("DELETE")) {
            if (body != null) {
                builder.method("DELETE", HttpRequest.BodyPublishers.ofString(body));
            } else {
                builder.DELETE();
            }
        } else {
            builder.method(method.toUpperCase(), body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
        }

        // Add required Gotit authorization header
        builder.header("X-GI-Authorization", authorizationKey);
        builder.header("Content-Type", "application/json");

        // Add custom headers
        if (headers != null) {
            headers.forEach(builder::header);
        }

        HttpRequest request = builder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
