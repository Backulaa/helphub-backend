package com.helphub.backend.modules.postmedia.dto.response;

import com.helphub.backend.common.enums.MediaFileType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PostMediaResponse {
    private UUID postId;
    private UUID mediaId;
    private String fileName;
    private String fileUrl;
    private MediaFileType fileType;
    private String mimeType;
    private Long fileSize;
    private String altText;
    private Boolean isPublic;
    private Integer displayOrder;
    private LocalDateTime attachedAt;
}
