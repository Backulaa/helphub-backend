package com.helphub.backend.modules.supportrequest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectSupportRequestRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 200, message = "Rejection reason must not exceed 200 characters")
    private String rejectionReason;
}