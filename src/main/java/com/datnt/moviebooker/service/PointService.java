package com.datnt.moviebooker.service;

import com.datnt.moviebooker.common.ApiWrapperResponse;
import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.dto.GetPointResponse;
import com.datnt.moviebooker.entity.Point;
import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.exception.BusinessException;
import com.datnt.moviebooker.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private static final Logger log = LoggerFactory.getLogger(PointService.class);
    private final PointRepository pointRepository;

    // Tao ban ghi diem cho user moi
    public Point create(User user) {
        if (user == null || user.getId() == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND, "User is null or does not have an ID");
        }
        Point point = new Point();
        point.setUser(user);
        point.setAvailablePoints(6);
        point.setRedeemedPoints(0);
        point.setTotalPoints(6);
        try {
            return pointRepository.save(point);
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create point record: " + e.getMessage());
        }
    }

    public ApiWrapperResponse<GetPointResponse> getPoint(long userId) {
        Point point = pointRepository.getPointByUserId(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND, "Point information not found for user"));

        GetPointResponse response = GetPointResponse.builder()
                .availablePoints(point.getAvailablePoints())
                .redeemedPoints(point.getRedeemedPoints())
                .totalPoints(point.getTotalPoints())
                .build();

        log.info("Successfully retrieved point information for user ID: {}", userId);
        return ApiWrapperResponse.success(ResponseCode.SUCCESS, response);
    }

    public Point findByUserId(long userId) {
        return pointRepository.getPointByUserId(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND, "Point information not found for user"));
    }

    public void savePoint(Point point) {
        if (point == null || point.getUser() == null || point.getUser().getId() == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND, "Point or user is null or does not have an ID");
        }
        try {
            pointRepository.save(point);
            log.info("Successfully saved point information for user ID: {}", point.getUser().getId());
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to save point record: " + e.getMessage());
        }
    }
}
