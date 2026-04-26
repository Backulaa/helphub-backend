package com.helphub.backend.modules.postreaction;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.postreaction.dto.request.CreatePostReactionRequest;
import com.helphub.backend.modules.postreaction.dto.request.UpdatePostReactionRequest;
import com.helphub.backend.modules.postreaction.dto.response.PostReactionCountResponse;
import com.helphub.backend.modules.postreaction.dto.response.PostReactionResponse;
import com.helphub.backend.security.model.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostReactionController {

    private final PostReactionService postReactionService;

    @PostMapping("/{postId}/reactions")
    public ResponseEntity<ApiResponse<PostReactionResponse>> createReaction(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId,
            @Valid @RequestBody CreatePostReactionRequest request) {

        PostReactionResponse response = postReactionService.createReaction(
                currentUser.getUserId(), postId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PostReactionResponse>builder()
                        .success(true)
                        .message("Reaction created successfully")
                        .data(response)
                        .build());
    }

    @PatchMapping("/{postId}/reactions")
    public ResponseEntity<ApiResponse<PostReactionResponse>> updateReaction(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId,
            @Valid @RequestBody UpdatePostReactionRequest request) {

        PostReactionResponse response = postReactionService.updateReaction(
                currentUser.getUserId(), postId, request);

        return ResponseEntity.ok(ApiResponse.<PostReactionResponse>builder()
                .success(true)
                .message("Reaction updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{postId}/reactions")
    public ResponseEntity<ApiResponse<Object>> deleteReaction(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId) {

        postReactionService.deleteReaction(currentUser.getUserId(), postId);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Reaction deleted successfully")
                .data(null)
                .build());
    }

    @GetMapping("/{postId}/reactions/me")
    public ResponseEntity<ApiResponse<PostReactionResponse>> getMyReaction(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId) {

        PostReactionResponse response = postReactionService.getMyReaction(
                currentUser.getUserId(), postId);

        return ResponseEntity.ok(ApiResponse.<PostReactionResponse>builder()
                .success(true)
                .message("My reaction fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{postId}/reactions/count")
    public ResponseEntity<ApiResponse<PostReactionCountResponse>> getReactionCount(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId) {

        PostReactionCountResponse response = postReactionService.getReactionCount(
                currentUser.getUserId(), postId);

        return ResponseEntity.ok(ApiResponse.<PostReactionCountResponse>builder()
                .success(true)
                .message("Reaction count fetched successfully")
                .data(response)
                .build());
    }
}
