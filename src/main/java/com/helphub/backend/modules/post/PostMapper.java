package com.helphub.backend.modules.post;

import com.helphub.backend.modules.post.dto.response.PostDetailResponse;
import com.helphub.backend.modules.post.dto.response.PostSummaryResponse;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostSummaryResponse toSummaryResponse(Post post) {
        User author = post.getAuthor();
        SupportRequest supportRequest = post.getSupportRequest();

        return PostSummaryResponse.builder()
                .id(post.getId())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .authorAvatarUrl(author.getAvatarUrl())
                .supportRequestId(supportRequest != null ? supportRequest.getId() : null)
                .supportRequestTitle(supportRequest != null ? supportRequest.getTitle() : null)
                .content(post.getContent())
                .visibility(post.getVisibility())
                .status(post.getStatus())
                .isActive(post.getIsActive())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public PostDetailResponse toDetailResponse(Post post) {
        User author = post.getAuthor();
        SupportRequest supportRequest = post.getSupportRequest();

        return PostDetailResponse.builder()
                .id(post.getId())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .authorAvatarUrl(author.getAvatarUrl())
                .supportRequestId(supportRequest != null ? supportRequest.getId() : null)
                .supportRequestTitle(supportRequest != null ? supportRequest.getTitle() : null)
                .content(post.getContent())
                .visibility(post.getVisibility())
                .status(post.getStatus())
                .isActive(post.getIsActive())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}