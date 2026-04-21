package com.helphub.backend.modules.media.dto.request;

import com.helphub.backend.common.enums.MediaFileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMediaRequest {

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @NotNull(message = "File type is required")
    private MediaFileType fileType;

    @NotBlank(message = "MIME type is required")
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    private String mimeType;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be greater than 0")
    private Long fileSize;

    @Size(max = 255, message = "Alt text must not exceed 255 characters")
    private String altText;

    @NotNull(message = "isPublic is required")
    private Boolean isPublic;
}