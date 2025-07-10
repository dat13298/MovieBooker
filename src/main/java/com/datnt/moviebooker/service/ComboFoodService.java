package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.ComboFoodRequest;
import com.datnt.moviebooker.dto.ComboFoodResponse;
import com.datnt.moviebooker.entity.ComboFood;
import com.datnt.moviebooker.mapper.ComboFoodMapper;
import com.datnt.moviebooker.repository.ComboFoodRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ComboFoodService {

    private static final Logger logger = LoggerFactory.getLogger(ComboFoodService.class);
    private static final String COMBO_CACHE_KEY = "combos";
    private static final long TTL = 10L;

    private final ComboFoodRepository comboFoodRepository;
    private final ComboFoodMapper comboFoodMapper;
    private final CloudinaryService cloudinaryService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final TypeReference<List<ComboFoodResponse>> COMBO_LIST_TYPE = new TypeReference<>() {};

    public List<ComboFoodResponse> getAllActiveCombos() {
        String json = redisTemplate.opsForValue().get(COMBO_CACHE_KEY);

        if (json != null) {
            try {
                return objectMapper.readValue(json, COMBO_LIST_TYPE);
            } catch (IOException e) {
                logger.error("Error parsing combo cache", e);
            }
        }

        List<ComboFoodResponse> combos = comboFoodRepository.findByIsActiveTrue()
                .stream()
                .map(comboFoodMapper::toResponse)
                .toList();

        try {
            redisTemplate.opsForValue().set(
                    COMBO_CACHE_KEY,
                    objectMapper.writeValueAsString(combos),
                    TTL, TimeUnit.MINUTES
            );
        } catch (IOException e) {
            logger.error("Error saving combo cache", e);
        }

        return combos;
    }

    public ComboFoodResponse findById(Long id) {
        ComboFood combo = comboFoodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Combo not found"));
        return comboFoodMapper.toResponse(combo);
    }

    public ComboFoodResponse createCombo(ComboFoodRequest request) {
        String imageUrl = null;
        String publicId = null;

        try {
            MultipartFile image = request.getImage();
            if (image != null && !image.isEmpty()) {
                Map<String, String> result = cloudinaryService.uploadImageWithResult(image);
                imageUrl = result.get("url");
                publicId = result.get("publicId");
            }

            ComboFood combo = comboFoodMapper.toEntity(request);
            combo.setImageUrl(imageUrl);
            combo = comboFoodRepository.save(combo);
            invalidateComboCache();
            return comboFoodMapper.toResponse(combo);
        } catch (Exception e) {
            if (publicId != null) cloudinaryService.deleteImage(publicId);
            throw new RuntimeException("Failed to create combo", e);
        }
    }

    public ComboFoodResponse updateCombo(Long id, ComboFoodRequest request) {
        return comboFoodRepository.findById(id).map(combo -> {
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                String url = cloudinaryService.uploadImage(request.getImage());
                combo.setImageUrl(url);
            }

            comboFoodMapper.updateEntity(combo, request);
            comboFoodRepository.save(combo);
            invalidateComboCache();
            return comboFoodMapper.toResponse(combo);
        }).orElseThrow(() -> new RuntimeException("Combo not found"));
    }

    public void deleteCombo(Long id) {
        comboFoodRepository.deleteById(id);
        invalidateComboCache();
    }

    private void invalidateComboCache() {
        redisTemplate.delete(COMBO_CACHE_KEY);
    }
}
