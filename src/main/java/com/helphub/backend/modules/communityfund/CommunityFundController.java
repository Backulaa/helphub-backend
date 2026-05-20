package com.helphub.backend.modules.communityfund;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.communityfund.dto.request.AddCommunityFundMemberRequest;
import com.helphub.backend.modules.communityfund.dto.request.CreateCommunityFundRequest;
import com.helphub.backend.modules.communityfund.dto.request.UpdateCommunityFundMemberRoleRequest;
import com.helphub.backend.modules.communityfund.dto.request.UpdateCommunityFundRequest;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundDetailResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundMemberResponse;
import com.helphub.backend.modules.communityfund.dto.response.CommunityFundSummaryResponse;
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
@RequestMapping("/api/v1/community-funds")
@RequiredArgsConstructor
@Validated
public class CommunityFundController {

    private final CommunityFundService communityFundService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<CommunityFundDetailResponse>> createCommunityFund(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateCommunityFundRequest request) {

        CommunityFundDetailResponse response = communityFundService.createCommunityFund(
                currentUser.getUserId(),
                request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CommunityFundDetailResponse>builder()
                        .success(true)
                        .message("Community fund created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommunityFundSummaryResponse>>> getAllCommunityFunds(
            @RequestParam(defaultValue = "true") Boolean activeOnly) {

        List<CommunityFundSummaryResponse> response = communityFundService.getAllCommunityFunds(activeOnly);

        return ResponseEntity.ok(ApiResponse.<List<CommunityFundSummaryResponse>>builder()
                .success(true)
                .message("Community funds fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/my-funds")
    public ResponseEntity<ApiResponse<List<CommunityFundSummaryResponse>>> getMyCommunityFunds(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<CommunityFundSummaryResponse> response = communityFundService.getMyCommunityFunds(
                currentUser.getUserId());

        return ResponseEntity.ok(ApiResponse.<List<CommunityFundSummaryResponse>>builder()
                .success(true)
                .message("My community funds fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommunityFundDetailResponse>> getCommunityFundById(
            @PathVariable @NotNull UUID id) {

        CommunityFundDetailResponse response = communityFundService.getCommunityFundById(id);

        return ResponseEntity.ok(ApiResponse.<CommunityFundDetailResponse>builder()
                .success(true)
                .message("Community fund fetched successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommunityFundDetailResponse>> updateCommunityFund(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody UpdateCommunityFundRequest request) {

        CommunityFundDetailResponse response = communityFundService.updateCommunityFund(
                currentUser.getUserId(),
                id,
                request);

        return ResponseEntity.ok(ApiResponse.<CommunityFundDetailResponse>builder()
                .success(true)
                .message("Community fund updated successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{fundId}/members")
    public ResponseEntity<ApiResponse<CommunityFundMemberResponse>> addMember(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID fundId,
            @Valid @RequestBody AddCommunityFundMemberRequest request) {

        CommunityFundMemberResponse response = communityFundService.addMember(
                currentUser.getUserId(),
                fundId,
                request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CommunityFundMemberResponse>builder()
                        .success(true)
                        .message("Community fund member added successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{fundId}/members")
    public ResponseEntity<ApiResponse<List<CommunityFundMemberResponse>>> getMembers(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID fundId) {

        List<CommunityFundMemberResponse> response = communityFundService.getMembers(
                currentUser.getUserId(),
                fundId);

        return ResponseEntity.ok(ApiResponse.<List<CommunityFundMemberResponse>>builder()
                .success(true)
                .message("Community fund members fetched successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/{fundId}/members/{userId}/role")
    public ResponseEntity<ApiResponse<CommunityFundMemberResponse>> updateMemberRole(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID fundId,
            @PathVariable @NotNull UUID userId,
            @Valid @RequestBody UpdateCommunityFundMemberRoleRequest request) {

        CommunityFundMemberResponse response = communityFundService.updateMemberRole(
                currentUser.getUserId(),
                fundId,
                userId,
                request);

        return ResponseEntity.ok(ApiResponse.<CommunityFundMemberResponse>builder()
                .success(true)
                .message("Community fund member role updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{fundId}/members/{userId}")
    public ResponseEntity<ApiResponse<Object>> removeMember(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID fundId,
            @PathVariable @NotNull UUID userId) {

        communityFundService.removeMember(
                currentUser.getUserId(),
                fundId,
                userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Community fund member removed successfully")
                .data(null)
                .build());
    }
}