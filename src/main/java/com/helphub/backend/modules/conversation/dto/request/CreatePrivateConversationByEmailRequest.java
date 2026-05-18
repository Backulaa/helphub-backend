package com.helphub.backend.modules.conversation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrivateConversationByEmailRequest {

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Receiver email is invalid")
    @Size(max = 50, message = "Receiver email must be at most 50 characters")
    private String receiverEmail;
}
