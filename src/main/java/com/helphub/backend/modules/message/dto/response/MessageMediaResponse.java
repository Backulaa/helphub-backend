package com.helphub.backend.modules.message.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageMediaResponse {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String mimeType;
    private Long fileSize;
    private String altText;
}