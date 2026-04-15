package com.helphub.backend.modules.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.modules.user.dto.request.UpdateProfileRequest;
import com.helphub.backend.modules.user.dto.request.UpdateUserRoleRequest;
import com.helphub.backend.modules.user.dto.request.UpdateUserStatusRequest;
import com.helphub.backend.modules.user.dto.response.UserDetailResponse;
import com.helphub.backend.modules.user.dto.response.UserProfileResponse;
import com.helphub.backend.modules.user.dto.response.UserSummaryResponse;

public interface UserService {

    UserProfileResponse getMyProfile(@NonNull UUID currentUserId);

    UserProfileResponse updateMyProfile(@NonNull UUID currentUserId, UpdateProfileRequest request);

    UserDetailResponse getUserById(@NonNull UUID userId);

    Page<UserSummaryResponse> getUsers(String keyword, UserRole role, @NonNull Pageable pageable);

    UserDetailResponse updateUserRole(@NonNull UUID targetUserId, UpdateUserRoleRequest request, @NonNull UUID adminId);

    UserDetailResponse updateUserStatus(@NonNull UUID targetUserId, UpdateUserStatusRequest request,
            @NonNull UUID adminId);
}