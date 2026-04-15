package com.helphub.backend.modules.user;

import java.util.UUID;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Sort;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.user.dto.request.UpdateProfileRequest;
import com.helphub.backend.modules.user.dto.request.UpdateUserRoleRequest;
import com.helphub.backend.modules.user.dto.request.UpdateUserStatusRequest;
import com.helphub.backend.modules.user.dto.response.UserDetailResponse;
import com.helphub.backend.modules.user.dto.response.UserProfileResponse;
import com.helphub.backend.modules.user.dto.response.UserSummaryResponse;
import com.helphub.backend.security.model.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

        private final UserService userService;

        @GetMapping("/me")
        public ApiResponse<UserProfileResponse> getMyProfile(
                        @AuthenticationPrincipal CustomUserDetails currentUser) {
                UserProfileResponse response = userService.getMyProfile(currentUser.getUserId());
                return ApiResponse.<UserProfileResponse>builder()
                                .success(true)
                                .message("Get my profile successfully")
                                .data(response)
                                .build();
        }

        @PatchMapping("/me")
        public ApiResponse<UserProfileResponse> updateMyProfile(
                        @AuthenticationPrincipal CustomUserDetails currentUser,
                        @Valid @RequestBody UpdateProfileRequest request) {
                UserProfileResponse response = userService.updateMyProfile(currentUser.getUserId(), request);
                return ApiResponse.<UserProfileResponse>builder()
                                .success(true)
                                .message("Update profile successfully")
                                .data(response)
                                .build();
        }

        @GetMapping("/{id}")
        public ApiResponse<UserDetailResponse> getUserById(@PathVariable @NonNull UUID id) {
                UserDetailResponse response = userService.getUserById(id);
                return ApiResponse.<UserDetailResponse>builder()
                                .success(true)
                                .message("Get user detail successfully")
                                .data(response)
                                .build();
        }

        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<Page<UserSummaryResponse>> getUsers(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) UserRole role,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt,desc") String sort) {
                String[] sortParts = sort.split(",");
                String sortField = sortParts[0];
                Sort.Direction direction = sortParts.length > 1
                                ? Sort.Direction.fromString(
                                                Objects.requireNonNull(sortParts[1], "Sort direction must not be null"))
                                : Sort.Direction.DESC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

                Page<UserSummaryResponse> response = userService.getUsers(keyword, role, pageable);

                return ApiResponse.<Page<UserSummaryResponse>>builder()
                                .success(true)
                                .message("Get users successfully")
                                .data(response)
                                .build();
        }

        @PatchMapping("/{id}/role")
        @PreAuthorize("hasRole('ADMIN')")
        @ResponseStatus(HttpStatus.OK)
        public ApiResponse<UserDetailResponse> updateUserRole(
                        @PathVariable @NonNull UUID id,
                        @Valid @RequestBody UpdateUserRoleRequest request,
                        @AuthenticationPrincipal CustomUserDetails currentUser) {
                UserDetailResponse response = userService.updateUserRole(id, request, currentUser.getUserId());
                return ApiResponse.<UserDetailResponse>builder()
                                .success(true)
                                .message("Update user role successfully")
                                .data(response)
                                .build();
        }

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasRole('ADMIN')")
        @ResponseStatus(HttpStatus.OK)
        public ApiResponse<UserDetailResponse> updateUserStatus(
                        @PathVariable @NonNull UUID id,
                        @Valid @RequestBody UpdateUserStatusRequest request,
                        @AuthenticationPrincipal CustomUserDetails currentUser) {
                UserDetailResponse response = userService.updateUserStatus(id, request, currentUser.getUserId());
                return ApiResponse.<UserDetailResponse>builder()
                                .success(true)
                                .message("Update user status successfully")
                                .data(response)
                                .build();
        }
}