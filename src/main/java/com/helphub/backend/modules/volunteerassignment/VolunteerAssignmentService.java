package com.helphub.backend.modules.volunteerassignment;

import com.helphub.backend.modules.volunteerassignment.dto.request.RejectVolunteerAssignmentRequest;
import com.helphub.backend.modules.volunteerassignment.dto.response.VolunteerAssignmentResponse;

import java.util.List;
import java.util.UUID;

public interface VolunteerAssignmentService {

    VolunteerAssignmentResponse applyToSupportRequest(UUID volunteerId, UUID supportRequestId);

    VolunteerAssignmentResponse approveVolunteer(UUID currentUserId, UUID supportRequestId, UUID volunteerId);

    VolunteerAssignmentResponse rejectVolunteer(
            UUID currentUserId,
            UUID supportRequestId,
            UUID volunteerId,
            RejectVolunteerAssignmentRequest request);

    VolunteerAssignmentResponse cancelMyAssignment(UUID volunteerId, UUID supportRequestId);

    VolunteerAssignmentResponse completeMyAssignment(UUID volunteerId, UUID supportRequestId);

    List<VolunteerAssignmentResponse> getMyAssignments(UUID volunteerId);

    List<VolunteerAssignmentResponse> getAssignmentsBySupportRequest(UUID currentUserId, UUID supportRequestId);
}