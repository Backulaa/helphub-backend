package com.helphub.backend.modules.postmedia;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.postmedia.dto.request.AttachMediaToPostRequest;
import com.helphub.backend.modules.postmedia.dto.response.PostMediaResponse;
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
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostMediaController {

    private final PostMediaService postMediaService;

    @PostMapping("/{postId}/media")
    public ResponseEntity<ApiResponse<PostMediaResponse>> attachMediaToPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId,
            @Valid @RequestBody AttachMediaToPostRequest request) {

        PostMediaResponse response = postMediaService.attachMediaToPost(
                currentUser.getUserId(), postId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PostMediaResponse>builder()
                        .success(true)
                        .message("Media attached to post successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{postId}/media")
    public ResponseEntity<ApiResponse<List<PostMediaResponse>>> getMediaByPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId) {

        List<PostMediaResponse> response = postMediaService.getMediaByPost(
                currentUser.getUserId(), postId);

        return ResponseEntity.ok(ApiResponse.<List<PostMediaResponse>>builder()
                .success(true)
                .message("Post media fetched successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{postId}/media/{mediaId}")
    public ResponseEntity<ApiResponse<Object>> removeMediaFromPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId,
            @PathVariable @NotNull UUID mediaId) {

        postMediaService.removeMediaFromPost(currentUser.getUserId(), postId, mediaId);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Media removed from post successfully")
                .data(null)
                .build());
    }
}
