package com.helphub.backend.modules.user;

import java.util.UUID;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.lang.NonNull;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.user.dto.request.UpdateProfileRequest;
import com.helphub.backend.modules.user.dto.request.UpdateUserRoleRequest;
import com.helphub.backend.modules.user.dto.request.UpdateUserStatusRequest;
import com.helphub.backend.modules.user.dto.response.UserDetailResponse;
import com.helphub.backend.modules.user.dto.response.UserProfileResponse;
import com.helphub.backend.modules.user.dto.response.UserSummaryResponse;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileResponse getMyProfile(@NonNull UUID currentUserId) {
        User user = getUserEntityById(currentUserId);
        return userMapper.toUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateMyProfile(@NonNull UUID currentUserId, UpdateProfileRequest request) {
        User user = getUserEntityById(currentUserId);

        boolean hasUpdate = false;

        if (request.getFullName() != null) {
            String fullName = request.getFullName().trim();
            if (fullName.isEmpty()) {
                throw new BadRequestException("Full name must not be blank");
            }
            user.setFullName(fullName);
            hasUpdate = true;
        }

        if (request.getPhone() != null) {
            user.setPhone(normalizeNullable(request.getPhone()));
            hasUpdate = true;
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(normalizeNullable(request.getAvatarUrl()));
            hasUpdate = true;
        }

        if (!hasUpdate) {
            throw new BadRequestException("At least one field must be provided for update");
        }

        User savedUser = userRepository.save(Objects.requireNonNull(user));
        return userMapper.toUserProfileResponse(savedUser);
    }

    @Override
    public UserDetailResponse getUserById(@NonNull UUID userId) {
        User user = getUserEntityById(userId);
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    public Page<UserSummaryResponse> getUsers(String keyword, UserRole role, @NonNull Pageable pageable) {
        Page<User> userPage;

        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasRole = role != null;

        if (hasKeyword && hasRole) {
            userPage = userRepository
                    .findByFullNameContainingIgnoreCaseAndRole(keyword.trim(), role, pageable);
        } else if (hasKeyword) {
            userPage = userRepository.findByFullNameContainingIgnoreCase(keyword.trim(), pageable);
        } else if (hasRole) {
            userPage = userRepository.findByRole(role, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(userMapper::toUserSummaryResponse);
    }

    @Override
    public UserDetailResponse updateUserRole(@NonNull UUID targetUserId, UpdateUserRoleRequest request,
            @NonNull UUID adminId) {
        User admin = getUserEntityById(adminId);
        User targetUser = getUserEntityById(targetUserId);

        validateAdminAction(admin);

        if (targetUser.getId().equals(admin.getId()) && request.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Admin cannot change their own role");
        }

        if (targetUser.getRole() == request.getRole()) {
            throw new BadRequestException("User already has this role");
        }

        targetUser.setRole(request.getRole());

        User savedUser = userRepository.save(targetUser);
        return userMapper.toUserDetailResponse(savedUser);
    }

    @Override
    public UserDetailResponse updateUserStatus(@NonNull UUID targetUserId, UpdateUserStatusRequest request,
            @NonNull UUID adminId) {
        User admin = getUserEntityById(adminId);
        User targetUser = getUserEntityById(targetUserId);

        validateAdminAction(admin);

        if (targetUser.getId().equals(admin.getId()) && Boolean.FALSE.equals(request.getIsActive())) {
            throw new BadRequestException("Admin cannot deactivate their own account");
        }

        if (targetUser.getIsActive().equals(request.getIsActive())) {
            throw new BadRequestException("User status is already set to the requested value");
        }

        targetUser.setIsActive(request.getIsActive());

        User savedUser = userRepository.save(targetUser);
        return userMapper.toUserDetailResponse(savedUser);
    }

    private User getUserEntityById(@NonNull UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateAdminAction(User admin) {
        if (admin.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only admin can perform this action");
        }
    }

    private String normalizeNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}