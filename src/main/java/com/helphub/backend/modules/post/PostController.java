package com.helphub.backend.modules.post;

import com.helphub.backend.common.payload.ApiResponse;
import com.helphub.backend.modules.post.dto.request.CreatePostRequest;
import com.helphub.backend.modules.post.dto.request.UpdatePostRequest;
import com.helphub.backend.modules.post.dto.response.PostDetailResponse;
import com.helphub.backend.modules.post.dto.response.PostSummaryResponse;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreatePostRequest request) {

        PostDetailResponse response = postService.createPost(currentUser.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PostDetailResponse>builder()
                        .success(true)
                        .message("Post created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostSummaryResponse>>> getAllPosts(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<PostSummaryResponse> response = postService.getAllPosts(currentUser.getUserId());

        return ResponseEntity.ok(ApiResponse.<List<PostSummaryResponse>>builder()
                .success(true)
                .message("Posts fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponse<List<PostSummaryResponse>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<PostSummaryResponse> response = postService.getMyPosts(currentUser.getUserId());

        return ResponseEntity.ok(ApiResponse.<List<PostSummaryResponse>>builder()
                .success(true)
                .message("My posts fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostById(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id) {

        PostDetailResponse response = postService.getPostById(currentUser.getUserId(), id);

        return ResponseEntity.ok(ApiResponse.<PostDetailResponse>builder()
                .success(true)
                .message("Post fetched successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updateMyPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody UpdatePostRequest request) {

        PostDetailResponse response = postService.updateMyPost(currentUser.getUserId(), id, request);

        return ResponseEntity.ok(ApiResponse.<PostDetailResponse>builder()
                .success(true)
                .message("Post updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteMyPost(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable @NotNull UUID id) {

        postService.deleteMyPost(currentUser.getUserId(), id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Post deleted successfully")
                .data(null)
                .build());
    }
}