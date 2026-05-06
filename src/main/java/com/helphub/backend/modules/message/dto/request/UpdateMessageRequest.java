package com.helphub.backend.modules.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 2000, message = "Message content must not exceed 2000 characters")
    private String content;
}