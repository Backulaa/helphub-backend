package com.helphub.backend.modules.postreaction;

import com.helphub.backend.modules.postreaction.dto.response.PostReactionResponse;
import com.helphub.backend.persistence.entity.PostReaction;
import com.helphub.backend.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostReactionMapper {

    public PostReactionResponse toResponse(PostReaction reaction) {
        User user = reaction.getUser();

        return PostReactionResponse.builder()
                .postId(reaction.getPost().getId())
                .userId(user.getId())
                .userName(user.getFullName())
                .type(reaction.getType())
                .createdAt(reaction.getCreatedAt())
                .updatedAt(reaction.getUpdatedAt())
                .build();
    }
}
