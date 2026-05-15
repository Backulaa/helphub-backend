package com.helphub.backend.modules.volunteerassignment;

import com.helphub.backend.modules.volunteerassignment.dto.response.VolunteerAssignmentResponse;
import com.helphub.backend.persistence.entity.Conversation;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.entity.VolunteerAssignment;
import org.springframework.stereotype.Component;

@Component
public class VolunteerAssignmentMapper {

    public VolunteerAssignmentResponse toResponse(VolunteerAssignment assignment) {
        SupportRequest supportRequest = assignment.getSupportRequest();
        User requester = supportRequest.getRequester();
        User volunteer = assignment.getVolunteer();
        User reviewer = assignment.getReviewedBy();
        Conversation conversation = assignment.getConversation();

        return VolunteerAssignmentResponse.builder()
                .supportRequestId(supportRequest.getId())
                .supportRequestTitle(supportRequest.getTitle())
                .supportRequestStatus(supportRequest.getStatus())

                .requesterId(requester.getId())
                .requesterName(requester.getFullName())

                .volunteerId(volunteer.getId())
                .volunteerName(volunteer.getFullName())
                .volunteerEmail(volunteer.getEmail())
                .volunteerPhone(volunteer.getPhone())

                .status(assignment.getStatus())

                .reviewedBy(reviewer != null ? reviewer.getId() : null)
                .reviewedByName(reviewer != null ? reviewer.getFullName() : null)
                .reviewedAt(assignment.getReviewedAt())
                .rejectionReason(assignment.getRejectionReason())

                .conversationId(conversation != null ? conversation.getId() : null)

                .assignedAt(assignment.getAssignedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}
