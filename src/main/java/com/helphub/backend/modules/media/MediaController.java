package com.helphub.backend.modules.media;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.media.dto.request.CreateMediaRequest;
import com.helphub.backend.modules.media.dto.request.UpdateMediaRequest;
import com.helphub.backend.modules.media.dto.response.MediaDetailResponse;
import com.helphub.backend.modules.media.dto.response.MediaSummaryResponse;
import com.helphub.backend.security.model.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Validated
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<ApiResponse<MediaDetailResponse>> createMedia(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateMediaRequest request) {

        MediaDetailResponse response = mediaService.createMedia(currentUser.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MediaDetailResponse>builder()
                        .success(true)
                        .message("Media created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaDetailResponse>> getMediaById(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id) {

        MediaDetailResponse response = mediaService.getMediaById(currentUser.getUserId(), id);

        return ResponseEntity.ok(ApiResponse.<MediaDetailResponse>builder()
                .success(true)
                .message("Media fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/my-media")
    public ResponseEntity<ApiResponse<List<MediaSummaryResponse>>> getMyMedia(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<MediaSummaryResponse> response = mediaService.getMyMedia(currentUser.getUserId());

        return ResponseEntity.ok(ApiResponse.<List<MediaSummaryResponse>>builder()
                .success(true)
                .message("My media fetched successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaDetailResponse>> updateMedia(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody UpdateMediaRequest request) {

        MediaDetailResponse response = mediaService.updateMedia(currentUser.getUserId(), id, request);

        return ResponseEntity.ok(ApiResponse.<MediaDetailResponse>builder()
                .success(true)
                .message("Media updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteMedia(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id) {

        mediaService.deleteMedia(currentUser.getUserId(), id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Media deleted successfully")
                .data(null)
                .build());
    }
}