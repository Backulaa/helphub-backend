package com.helphub.backend.modules.supportneed;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.supportneed.dto.request.CreateSupportNeedContributionRequest;
import com.helphub.backend.modules.supportneed.dto.request.CreateSupportNeedRequest;
import com.helphub.backend.modules.supportneed.dto.request.UpdateSupportNeedRequest;
import com.helphub.backend.modules.supportneed.dto.response.SupportNeedContributionResponse;
import com.helphub.backend.modules.supportneed.dto.response.SupportNeedResponse;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class SupportNeedController {

    private final SupportNeedService supportNeedService;

    @PreAuthorize("hasRole('REQUESTER')")
    @PostMapping("/support-requests/{supportRequestId}/needs")
    public ResponseEntity<ApiResponse<SupportNeedResponse>> createSupportNeed(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID supportRequestId,
            @Valid @RequestBody CreateSupportNeedRequest request) {

        SupportNeedResponse response = supportNeedService.createSupportNeed(
                currentUser.getUserId(),
                supportRequestId,
                request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SupportNeedResponse>builder()
                        .success(true)
                        .message("Support need created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/support-requests/{supportRequestId}/needs")
    public ResponseEntity<ApiResponse<List<SupportNeedResponse>>> getSupportNeedsBySupportRequest(
            @PathVariable @NotNull UUID supportRequestId) {

        List<SupportNeedResponse> response = supportNeedService.getSupportNeedsBySupportRequest(supportRequestId);

        return ResponseEntity.ok(ApiResponse.<List<SupportNeedResponse>>builder()
                .success(true)
                .message("Support needs fetched successfully")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('REQUESTER')")
    @PutMapping("/support-needs/{needId}")
    public ResponseEntity<ApiResponse<SupportNeedResponse>> updateSupportNeed(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID needId,
            @Valid @RequestBody UpdateSupportNeedRequest request) {

        SupportNeedResponse response = supportNeedService.updateSupportNeed(
                currentUser.getUserId(),
                needId,
                request);

        return ResponseEntity.ok(ApiResponse.<SupportNeedResponse>builder()
                .success(true)
                .message("Support need updated successfully")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('REQUESTER')")
    @DeleteMapping("/support-needs/{needId}")
    public ResponseEntity<ApiResponse<Object>> deleteSupportNeed(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID needId) {

        supportNeedService.deleteSupportNeed(currentUser.getUserId(), needId);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Support need deleted successfully")
                .data(null)
                .build());
    }

    @PreAuthorize("hasRole('VOLUNTEER') or hasRole('COLLABORATOR')")
    @PostMapping("/support-needs/{needId}/contributions")
    public ResponseEntity<ApiResponse<SupportNeedContributionResponse>> contributeToSupportNeed(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID needId,
            @Valid @RequestBody CreateSupportNeedContributionRequest request) {

        SupportNeedContributionResponse response = supportNeedService.contributeToSupportNeed(
                currentUser.getUserId(),
                needId,
                request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SupportNeedContributionResponse>builder()
                        .success(true)
                        .message("Support need contribution created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/support-needs/{needId}/contributions")
    public ResponseEntity<ApiResponse<List<SupportNeedContributionResponse>>> getContributionsBySupportNeed(
            @PathVariable @NotNull UUID needId) {

        List<SupportNeedContributionResponse> response = supportNeedService.getContributionsBySupportNeed(needId);

        return ResponseEntity.ok(ApiResponse.<List<SupportNeedContributionResponse>>builder()
                .success(true)
                .message("Support need contributions fetched successfully")
                .data(response)
                .build());
    }
}