package com.helphub.backend.modules.postcomment;

import java.util.List;
import java.util.UUID;

import com.helphub.backend.modules.postcomment.dto.request.CreatePostCommentRequest;
import com.helphub.backend.modules.postcomment.dto.request.UpdatePostCommentRequest;
import com.helphub.backend.modules.postcomment.dto.response.PostCommentResponse;

public interface PostCommentService {

    PostCommentResponse createComment(UUID currentUserId, UUID postId, CreatePostCommentRequest request);

    List<PostCommentResponse> getCommentsByPost(UUID currentUserId, UUID postId);

    List<PostCommentResponse> getRepliesByComment(UUID currentUserId, UUID commentId);

    PostCommentResponse updateMyComment(UUID currentUserId, UUID commentId, UpdatePostCommentRequest request);

    void deleteMyComment(UUID currentUserId, UUID commentId);
}
