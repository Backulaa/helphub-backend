package com.helphub.backend.modules.media.dto.response;

import com.helphub.backend.common.enums.MediaFileType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MediaSummaryResponse {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private MediaFileType fileType;
    private String mimeType;
    private Long fileSize;
    private Boolean isPublic;
    private LocalDateTime createdAt;
}