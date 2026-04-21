package com.helphub.backend.modules.media;

import com.helphub.backend.modules.media.dto.response.MediaDetailResponse;
import com.helphub.backend.modules.media.dto.response.MediaSummaryResponse;
import com.helphub.backend.persistence.entity.Media;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {

    public MediaSummaryResponse toSummaryResponse(Media media) {
        return MediaSummaryResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .fileUrl(media.getFileUrl())
                .fileType(media.getFileType())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .isPublic(media.getIsPublic())
                .createdAt(media.getCreatedAt())
                .build();
    }

    public MediaDetailResponse toDetailResponse(Media media) {
        return MediaDetailResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .fileUrl(media.getFileUrl())
                .fileType(media.getFileType())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .uploadedBy(media.getUploadedBy().getId())
                .altText(media.getAltText())
                .isPublic(media.getIsPublic())
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }
}