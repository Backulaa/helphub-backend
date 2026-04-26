package com.helphub.backend.modules.postreaction;

import java.util.UUID;

import com.helphub.backend.modules.postreaction.dto.request.CreatePostReactionRequest;
import com.helphub.backend.modules.postreaction.dto.request.UpdatePostReactionRequest;
import com.helphub.backend.modules.postreaction.dto.response.PostReactionCountResponse;
import com.helphub.backend.modules.postreaction.dto.response.PostReactionResponse;

public interface PostReactionService {

    PostReactionResponse createReaction(UUID currentUserId, UUID postId, CreatePostReactionRequest request);

    PostReactionResponse updateReaction(UUID currentUserId, UUID postId, UpdatePostReactionRequest request);

    void deleteReaction(UUID currentUserId, UUID postId);

    PostReactionResponse getMyReaction(UUID currentUserId, UUID postId);

    PostReactionCountResponse getReactionCount(UUID currentUserId, UUID postId);
}