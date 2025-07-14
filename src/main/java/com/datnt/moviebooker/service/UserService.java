package com.datnt.moviebooker.service;

import com.datnt.moviebooker.common.ResponseCode;
import com.datnt.moviebooker.constant.Gender;
import com.datnt.moviebooker.constant.Role;
import com.datnt.moviebooker.dto.AdminCreateUserRequest;
import com.datnt.moviebooker.dto.UserRegisterRequest;
import com.datnt.moviebooker.dto.UserResponse;
import com.datnt.moviebooker.entity.User;
import com.datnt.moviebooker.exception.BusinessException;
import com.datnt.moviebooker.mapper.UserMapper;
import com.datnt.moviebooker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PointService pointService;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    public UserResponse registerUser(UserRegisterRequest request) {
        try {
            return createUserInternal(request, Role.ROLE_USER);
        } catch (DataIntegrityViolationException ex) {
            handleConstraintViolation(ex);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    public UserResponse updateUserByAdmin(Long userId, Map<String, Object> updates) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

            if (updates.containsKey("username")) {
                user.setUsername((String) updates.get("username"));
            }
            if (updates.containsKey("email")) {
                user.setEmail((String) updates.get("email"));
            }
            if (updates.containsKey("phoneNumber")) {
                user.setPhoneNumber((String) updates.get("phoneNumber"));
            }
            if (updates.containsKey("gender")) {
                user.setGender(Gender.valueOf((String) updates.get("gender")));
            }
            if (updates.containsKey("DoB")) {
                String dobStr = (String) updates.get("DoB");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date dob = formatter.parse(dobStr);
                    user.setDoB(dob);
                } catch (ParseException e) {
                    throw new BusinessException(ResponseCode.CONFLICT, "Ngày sinh không hợp lệ (yyyy-MM-dd)");
                }
            }
            if (updates.containsKey("role")) {
                user.setRole(Role.valueOf((String) updates.get("role")));
            }

            userRepository.save(user);
            return userMapper.toResponse(user);

        } catch (DataIntegrityViolationException ex) {
            handleConstraintViolation(ex);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    public UserResponse createUserByAdmin(AdminCreateUserRequest request) {
        try {
            return createUserInternal(request, request.getRole());
        } catch (DataIntegrityViolationException ex) {
            handleConstraintViolation(ex);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    public UserResponse updateCurrentUser(Map<String, Object> updates, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

            if (updates.containsKey("username")) {
                user.setUsername((String) updates.get("username"));
            }
            if (updates.containsKey("email")) {
                user.setEmail((String) updates.get("email"));
            }
            if (updates.containsKey("phone")) {
                user.setPhoneNumber((String) updates.get("phone"));
            }
            if (updates.containsKey("gender")) {
                user.setGender(Gender.valueOf((String) updates.get("gender")));
            }
            if (updates.containsKey("dob")) {
                String dobStr = (String) updates.get("dob");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date dob = formatter.parse(dobStr);
                    user.setDoB(dob);
                } catch (ParseException e) {
                    throw new BusinessException(ResponseCode.CONFLICT, "Ngày sinh không hợp lệ (yyyy-MM-dd)");
                }
            }

            userRepository.save(user);
            return userMapper.toResponse(user);
        } catch (DataIntegrityViolationException ex) {
            handleConstraintViolation(ex);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }


    private void handleConstraintViolation(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof org.hibernate.exception.ConstraintViolationException constraintEx) {
            String constraintName = constraintEx.getConstraintName();
            if (constraintName != null) {
                String cleanName = extractConstraintName(constraintName); // strip prefix "users."
                switch (cleanName) {
                    case "UK_user_email":
                        throw new BusinessException(ResponseCode.EMAIL_ALREADY_EXISTS);
                    case "UK_user_username":
                        throw new BusinessException(ResponseCode.USERNAME_ALREADY_EXISTS);
                    case "UK_user_phone":
                        throw new BusinessException(ResponseCode.PHONE_ALREADY_EXISTS);
                    default:
                        throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR,
                                "Vi phạm ràng buộc: " + cleanName);
                }
            }
        }

        throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR, "Lỗi hệ thống");
    }

    private String extractConstraintName(String rawName) {
        int dotIndex = rawName.indexOf(".");
        return dotIndex != -1 ? rawName.substring(dotIndex + 1) : rawName;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    public boolean isAdmin(Long userId) {
        return findById(userId).getRole().equals(Role.ROLE_ADMIN);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    private UserResponse createUserInternal(UserRegisterRequest request, Role role) {
        User user = userMapper.toEntity(request, role);
        userRepository.save(user);
        pointService.create(user); // Tao ban ghi diem cho user moi
        return userMapper.toResponse(user);
    }

    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }
}
