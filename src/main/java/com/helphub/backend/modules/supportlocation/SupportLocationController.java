package com.helphub.backend.modules.supportlocation;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.supportlocation.dto.request.CreateSupportLocationRequest;
import com.helphub.backend.modules.supportlocation.dto.request.UpdateSupportLocationRequest;
import com.helphub.backend.modules.supportlocation.dto.request.UpdateSupportLocationStatusRequest;
import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationDetailResponse;
import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationSummaryResponse;
import com.helphub.backend.security.model.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/support-locations")
@RequiredArgsConstructor
@Validated
public class SupportLocationController {

    private final SupportLocationService supportLocationService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<SupportLocationDetailResponse>> createSupportLocation(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateSupportLocationRequest request) {

        SupportLocationDetailResponse response = supportLocationService.createSupportLocation(
                currentUser.getUserId(),
                request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SupportLocationDetailResponse>builder()
                        .success(true)
                        .message("Support location created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupportLocationSummaryResponse>>> getAllSupportLocations(
            @RequestParam(defaultValue = "true") Boolean activeOnly) {

        List<SupportLocationSummaryResponse> response = supportLocationService.getAllSupportLocations(
                activeOnly);

        return ResponseEntity.ok(ApiResponse.<List<SupportLocationSummaryResponse>>builder()
                .success(true)
                .message("Support locations fetched successfully")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
    @GetMapping("/my-created")
    public ResponseEntity<ApiResponse<List<SupportLocationSummaryResponse>>> getMyCreatedSupportLocations(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<SupportLocationSummaryResponse> response = supportLocationService.getMyCreatedSupportLocations(
                currentUser.getUserId());

        return ResponseEntity.ok(ApiResponse.<List<SupportLocationSummaryResponse>>builder()
                .success(true)
                .message("My created support locations fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupportLocationDetailResponse>> getSupportLocationById(
            @PathVariable @NotNull UUID id) {

        SupportLocationDetailResponse response = supportLocationService.getSupportLocationById(id);

        return ResponseEntity.ok(ApiResponse.<SupportLocationDetailResponse>builder()
                .success(true)
                .message("Support location fetched successfully")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupportLocationDetailResponse>> updateSupportLocation(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody UpdateSupportLocationRequest request) {

        SupportLocationDetailResponse response = supportLocationService.updateSupportLocation(
                currentUser.getUserId(),
                id,
                request);

        return ResponseEntity.ok(ApiResponse.<SupportLocationDetailResponse>builder()
                .success(true)
                .message("Support location updated successfully")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SupportLocationDetailResponse>> updateSupportLocationStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody UpdateSupportLocationStatusRequest request) {

        SupportLocationDetailResponse response = supportLocationService.updateSupportLocationStatus(
                currentUser.getUserId(),
                id,
                request);

        return ResponseEntity.ok(ApiResponse.<SupportLocationDetailResponse>builder()
                .success(true)
                .message("Support location status updated successfully")
                .data(response)
                .build());
    }
}