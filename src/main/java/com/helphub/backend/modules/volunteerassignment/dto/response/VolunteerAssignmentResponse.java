package com.helphub.backend.modules.volunteerassignment.dto.response;

import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.common.enums.VolunteerAssignmentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerAssignmentResponse {

    private UUID supportRequestId;
    private String supportRequestTitle;
    private SupportRequestStatus supportRequestStatus;

    private UUID requesterId;
    private String requesterName;

    private UUID volunteerId;
    private String volunteerName;
    private String volunteerEmail;
    private String volunteerPhone;

    private VolunteerAssignmentStatus status;

    private UUID reviewedBy;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private String rejectionReason;

    private UUID conversationId;

    private LocalDateTime assignedAt;
    private LocalDateTime updatedAt;
}