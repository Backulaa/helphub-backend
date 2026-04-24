package com.helphub.backend.modules.postcomment;

import com.helphub.backend.modules.postcomment.dto.response.PostCommentResponse;
import com.helphub.backend.persistence.entity.PostComment;
import com.helphub.backend.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostCommentMapper {

    public PostCommentResponse toResponse(PostComment comment) {
        User user = comment.getUser();

        return PostCommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(user.getId())
                .userName(user.getFullName())
                .userAvatarUrl(user.getAvatarUrl())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
