package com.helphub.backend.modules.postmedia;

import java.util.List;
import java.util.UUID;

import com.helphub.backend.modules.postmedia.dto.request.AttachMediaToPostRequest;
import com.helphub.backend.modules.postmedia.dto.response.PostMediaResponse;

public interface PostMediaService {

    PostMediaResponse attachMediaToPost(UUID currentUserId, UUID postId, AttachMediaToPostRequest request);

    List<PostMediaResponse> getMediaByPost(UUID currentUserId, UUID postId);

    void removeMediaFromPost(UUID currentUserId, UUID postId, UUID mediaId);
}
