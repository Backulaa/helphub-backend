package com.helphub.backend.modules.postmedia;

import com.helphub.backend.modules.postmedia.dto.response.PostMediaResponse;
import com.helphub.backend.persistence.entity.Media;
import com.helphub.backend.persistence.entity.PostMedia;
import org.springframework.stereotype.Component;

@Component
public class PostMediaMapper {

    public PostMediaResponse toResponse(PostMedia postMedia) {
        Media media = postMedia.getMedia();

        return PostMediaResponse.builder()
                .postId(postMedia.getPost().getId())
                .mediaId(media.getId())
                .fileName(media.getFileName())
                .fileUrl(media.getFileUrl())
                .fileType(media.getFileType())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .altText(media.getAltText())
                .isPublic(media.getIsPublic())
                .displayOrder(postMedia.getDisplayOrder())
                .attachedAt(postMedia.getCreatedAt())
                .build();
    }
}
