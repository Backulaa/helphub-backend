package com.helphub.backend.modules.postcomment;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.postcomment.dto.request.CreatePostCommentRequest;
import com.helphub.backend.modules.postcomment.dto.request.UpdatePostCommentRequest;
import com.helphub.backend.modules.postcomment.dto.response.PostCommentResponse;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<PostCommentResponse>> createComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId,
            @Valid @RequestBody CreatePostCommentRequest request) {

        PostCommentResponse response = postCommentService.createComment(
                currentUser.getUserId(), postId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PostCommentResponse>builder()
                        .success(true)
                        .message("Comment created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<PostCommentResponse>>> getCommentsByPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID postId) {

        List<PostCommentResponse> response = postCommentService.getCommentsByPost(
                currentUser.getUserId(), postId);

        return ResponseEntity.ok(ApiResponse.<List<PostCommentResponse>>builder()
                .success(true)
                .message("Post comments fetched successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<PostCommentResponse>> updateMyComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID commentId,
            @Valid @RequestBody UpdatePostCommentRequest request) {

        PostCommentResponse response = postCommentService.updateMyComment(
                currentUser.getUserId(), commentId, request);

        return ResponseEntity.ok(ApiResponse.<PostCommentResponse>builder()
                .success(true)
                .message("Comment updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<PostCommentResponse>>> getRepliesByComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID commentId) {

        List<PostCommentResponse> response = postCommentService.getRepliesByComment(
                currentUser.getUserId(), commentId);

        return ResponseEntity.ok(ApiResponse.<List<PostCommentResponse>>builder()
                .success(true)
                .message("Comment replies fetched successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Object>> deleteMyComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID commentId) {

        postCommentService.deleteMyComment(currentUser.getUserId(), commentId);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Comment deleted successfully")
                .data(null)
                .build());
    }
}