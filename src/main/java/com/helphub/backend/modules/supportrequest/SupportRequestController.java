package com.helphub.backend.modules.supportrequest;

import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.supportrequest.dto.request.AssignSupportRequestToSupportLocationRequest;
import com.helphub.backend.modules.supportrequest.dto.request.CreateSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.request.RejectSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.request.UpdateSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestDetailResponse;
import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestSummaryResponse;
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
@RequestMapping("/api/v1/support-requests")
@RequiredArgsConstructor
@Validated
public class SupportRequestController {

        private final SupportRequestService supportRequestService;

        @PreAuthorize("hasRole('REQUESTER')")
        @PostMapping
        public ResponseEntity<ApiResponse<SupportRequestDetailResponse>> createSupportRequest(
                        @AuthenticationPrincipal CustomUserDetails currentUser,
                        @Valid @RequestBody CreateSupportRequestRequest request) {

                SupportRequestDetailResponse response = supportRequestService.createSupportRequest(
                                currentUser.getUserId(), request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.<SupportRequestDetailResponse>builder()
                                                .success(true)
                                                .message("Support request created successfully")
                                                .data(response)
                                                .build());
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<SupportRequestSummaryResponse>>> getAllSupportRequests(
                        @RequestParam(required = false) SupportRequestStatus status) {

                List<SupportRequestSummaryResponse> response = supportRequestService.getAllSupportRequests(status);

                return ResponseEntity.ok(ApiResponse.<List<SupportRequestSummaryResponse>>builder()
                                .success(true)
                                .message("Support requests fetched successfully")
                                .data(response)
                                .build());
        }

        @PreAuthorize("hasRole('REQUESTER')")
        @GetMapping("/my-requests")
        public ResponseEntity<ApiResponse<List<SupportRequestSummaryResponse>>> getMySupportRequests(
                        @AuthenticationPrincipal CustomUserDetails currentUser) {

                List<SupportRequestSummaryResponse> response = supportRequestService.getMySupportRequests(
                                currentUser.getUserId());

                return ResponseEntity.ok(ApiResponse.<List<SupportRequestSummaryResponse>>builder()
                                .success(true)
                                .message("My support requests fetched successfully")
                                .data(response)
                                .build());
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<SupportRequestDetailResponse>> getSupportRequestById(
                        @PathVariable @NotNull UUID id) {

                SupportRequestDetailResponse response = supportRequestService.getSupportRequestById(id);

                return ResponseEntity.ok(ApiResponse.<SupportRequestDetailResponse>builder()
                                .success(true)
                                .message("Support request fetched successfully")
                                .data(response)
                                .build());
        }

        @PreAuthorize("hasRole('REQUESTER')")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<SupportRequestDetailResponse>> updateMySupportRequest(
                        @AuthenticationPrincipal CustomUserDetails currentUser,
                        @PathVariable @NotNull UUID id,
                        @Valid @RequestBody UpdateSupportRequestRequest request) {

                SupportRequestDetailResponse response = supportRequestService.updateMySupportRequest(
                                currentUser.getUserId(), id, request);

                return ResponseEntity.ok(ApiResponse.<SupportRequestDetailResponse>builder()
                                .success(true)
                                .message("Support request updated successfully")
                                .data(response)
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
        @PatchMapping("/{id}/approve")
        public ResponseEntity<ApiResponse<SupportRequestDetailResponse>> approveSupportRequest(
                        @AuthenticationPrincipal CustomUserDetails currentUser,
                        @PathVariable @NotNull UUID id) {

                SupportRequestDetailResponse response = supportRequestService.approveSupportRequest(
                                currentUser.getUserId(), id);

                return ResponseEntity.ok(ApiResponse.<SupportRequestDetailResponse>builder()
                                .success(true)
                                .message("Support request approved successfully")
                                .data(response)
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
        @PatchMapping("/{id}/reject")
        public ResponseEntity<ApiResponse<SupportRequestDetailResponse>> rejectSupportRequest(
                        @AuthenticationPrincipal CustomUserDetails currentUser,
                        @PathVariable @NotNull UUID id,
                        @Valid @RequestBody RejectSupportRequestRequest request) {

                SupportRequestDetailResponse response = supportRequestService.rejectSupportRequest(
                                currentUser.getUserId(), id, request);

                return ResponseEntity.ok(ApiResponse.<SupportRequestDetailResponse>builder()
                                .success(true)
                                .message("Support request rejected successfully")
                                .data(response)
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN') or hasRole('COLLABORATOR')")
        @PatchMapping("/{id}/assign-support-location")
        public ResponseEntity<ApiResponse<SupportRequestDetailResponse>> assignSupportRequestToSupportLocation(
                        @AuthenticationPrincipal CustomUserDetails currentUser,
                        @PathVariable @NotNull UUID id,
                        @Valid @RequestBody AssignSupportRequestToSupportLocationRequest request) {

                SupportRequestDetailResponse response = supportRequestService.assignSupportRequestToSupportLocation(
                                currentUser.getUserId(), id, request);

                return ResponseEntity.ok(ApiResponse.<SupportRequestDetailResponse>builder()
                                .success(true)
                                .message("Support request assigned to support location successfully")
                                .data(response)
                                .build());
        }
}