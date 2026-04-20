package com.helphub.backend.modules.post;

import com.helphub.backend.modules.post.dto.request.CreatePostRequest;
import com.helphub.backend.modules.post.dto.request.UpdatePostRequest;
import com.helphub.backend.modules.post.dto.response.PostDetailResponse;
import com.helphub.backend.modules.post.dto.response.PostSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface PostService {

    PostDetailResponse createPost(UUID authorId, CreatePostRequest request);

    List<PostSummaryResponse> getAllPosts(UUID currentUserId);

    List<PostSummaryResponse> getMyPosts(UUID currentUserId);

    PostDetailResponse getPostById(UUID currentUserId, UUID postId);

    PostDetailResponse updateMyPost(UUID currentUserId, UUID postId, UpdatePostRequest request);

    void deleteMyPost(UUID currentUserId, UUID postId);
}