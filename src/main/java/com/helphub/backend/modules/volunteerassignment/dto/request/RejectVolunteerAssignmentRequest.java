package com.helphub.backend.modules.volunteerassignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectVolunteerAssignmentRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 200, message = "Rejection reason must not exceed 200 characters")
    private String rejectionReason;
}
