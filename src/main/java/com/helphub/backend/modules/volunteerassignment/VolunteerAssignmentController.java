package com.helphub.backend.modules.volunteerassignment;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.volunteerassignment.dto.request.RejectVolunteerAssignmentRequest;
import com.helphub.backend.modules.volunteerassignment.dto.response.VolunteerAssignmentResponse;
import com.helphub.backend.security.model.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/volunteer-assignments")
@RequiredArgsConstructor
@Validated
public class VolunteerAssignmentController {

    private final VolunteerAssignmentService volunteerAssignmentService;

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/support-requests/{supportRequestId}/apply")
    public ApiResponse<VolunteerAssignmentResponse> applyToSupportRequest(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID supportRequestId) {

        VolunteerAssignmentResponse response = volunteerAssignmentService.applyToSupportRequest(
                currentUser.getUserId(),
                supportRequestId);

        return ApiResponse.<VolunteerAssignmentResponse>builder()
                .success(true)
                .message("Applied to support request successfully")
                .data(response)
                .build();
    }

    @PatchMapping("/support-requests/{supportRequestId}/volunteers/{volunteerId}/approve")
    public ApiResponse<VolunteerAssignmentResponse> approveVolunteer(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID supportRequestId,
            @PathVariable @NotNull UUID volunteerId) {

        VolunteerAssignmentResponse response = volunteerAssignmentService.approveVolunteer(
                currentUser.getUserId(),
                supportRequestId,
                volunteerId);

        return ApiResponse.<VolunteerAssignmentResponse>builder()
                .success(true)
                .message("Volunteer approved successfully")
                .data(response)
                .build();
    }

    @PatchMapping("/support-requests/{supportRequestId}/volunteers/{volunteerId}/reject")
    public ApiResponse<VolunteerAssignmentResponse> rejectVolunteer(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID supportRequestId,
            @PathVariable @NotNull UUID volunteerId,
            @Valid @RequestBody RejectVolunteerAssignmentRequest request) {

        VolunteerAssignmentResponse response = volunteerAssignmentService.rejectVolunteer(
                currentUser.getUserId(),
                supportRequestId,
                volunteerId,
                request);

        return ApiResponse.<VolunteerAssignmentResponse>builder()
                .success(true)
                .message("Volunteer rejected successfully")
                .data(response)
                .build();
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PatchMapping("/support-requests/{supportRequestId}/cancel")
    public ApiResponse<VolunteerAssignmentResponse> cancelMyAssignment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID supportRequestId) {

        VolunteerAssignmentResponse response = volunteerAssignmentService.cancelMyAssignment(
                currentUser.getUserId(),
                supportRequestId);

        return ApiResponse.<VolunteerAssignmentResponse>builder()
                .success(true)
                .message("Volunteer assignment cancelled successfully")
                .data(response)
                .build();
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PatchMapping("/support-requests/{supportRequestId}/complete")
    public ApiResponse<VolunteerAssignmentResponse> completeMyAssignment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID supportRequestId) {

        VolunteerAssignmentResponse response = volunteerAssignmentService.completeMyAssignment(
                currentUser.getUserId(),
                supportRequestId);

        return ApiResponse.<VolunteerAssignmentResponse>builder()
                .success(true)
                .message("Volunteer assignment completed successfully")
                .data(response)
                .build();
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @GetMapping("/my-assignments")
    public ApiResponse<List<VolunteerAssignmentResponse>> getMyAssignments(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<VolunteerAssignmentResponse> response = volunteerAssignmentService.getMyAssignments(
                currentUser.getUserId());

        return ApiResponse.<List<VolunteerAssignmentResponse>>builder()
                .success(true)
                .message("My volunteer assignments fetched successfully")
                .data(response)
                .build();
    }

    @GetMapping("/support-requests/{supportRequestId}")
    public ApiResponse<List<VolunteerAssignmentResponse>> getAssignmentsBySupportRequest(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID supportRequestId) {

        List<VolunteerAssignmentResponse> response = volunteerAssignmentService.getAssignmentsBySupportRequest(
                currentUser.getUserId(),
                supportRequestId);

        return ApiResponse.<List<VolunteerAssignmentResponse>>builder()
                .success(true)
                .message("Volunteer assignments fetched successfully")
                .data(response)
                .build();
    }
}