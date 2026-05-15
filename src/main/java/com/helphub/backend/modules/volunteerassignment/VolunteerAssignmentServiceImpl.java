package com.helphub.backend.modules.volunteerassignment;

import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.enums.VolunteerAssignmentStatus;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.common.util.DateTimeUtils;
import com.helphub.backend.modules.conversation.ConversationService;
import com.helphub.backend.modules.notification.NotificationService;
import com.helphub.backend.modules.volunteerassignment.dto.request.RejectVolunteerAssignmentRequest;
import com.helphub.backend.modules.volunteerassignment.dto.response.VolunteerAssignmentResponse;
import com.helphub.backend.persistence.entity.Conversation;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.entity.VolunteerAssignment;
import com.helphub.backend.persistence.entity.VolunteerAssignmentId;
import com.helphub.backend.persistence.repository.SupportRequestRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import com.helphub.backend.persistence.repository.VolunteerAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VolunteerAssignmentServiceImpl implements VolunteerAssignmentService {

    private final VolunteerAssignmentRepository volunteerAssignmentRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final VolunteerAssignmentMapper volunteerAssignmentMapper;
    private final ConversationService conversationService;
    private final NotificationService notificationService;

    @Override
    public VolunteerAssignmentResponse applyToSupportRequest(UUID volunteerId, UUID supportRequestId) {
        User volunteer = getUserById(volunteerId);
        validateVolunteerRole(volunteer);

        SupportRequest supportRequest = getSupportRequestById(supportRequestId);

        if (supportRequest.getStatus() != SupportRequestStatus.APPROVED) {
            throw new BadRequestException("Only approved support request can be applied");
        }

        if (supportRequest.getRequester().getId().equals(volunteer.getId())) {
            throw new BadRequestException("Requester cannot volunteer for their own support request");
        }

        if (volunteerAssignmentRepository.existsBySupportRequestAndVolunteer(supportRequest, volunteer)) {
            throw new BadRequestException("You have already applied to this support request");
        }

        VolunteerAssignment assignment = VolunteerAssignment.builder()
                .id(new VolunteerAssignmentId(supportRequest.getId(), volunteer.getId()))
                .supportRequest(supportRequest)
                .volunteer(volunteer)
                .status(VolunteerAssignmentStatus.PENDING)
                .build();

        VolunteerAssignment savedAssignment = volunteerAssignmentRepository.save(Objects.requireNonNull(assignment));

        notificationService.createNotification(
                supportRequest.getRequester().getId(),
                volunteer.getFullName() + " wants to support your request: " + supportRequest.getTitle(),
                "VOLUNTEER_ASSIGNMENT",
                supportRequest.getId(),
                "/support-requests/" + supportRequest.getId());

        return volunteerAssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public VolunteerAssignmentResponse approveVolunteer(UUID currentUserId, UUID supportRequestId, UUID volunteerId) {
        User currentUser = getUserById(currentUserId);
        SupportRequest supportRequest = getSupportRequestById(supportRequestId);
        VolunteerAssignment assignment = getAssignment(supportRequestId, volunteerId);
        Conversation conversation = conversationService.createOrGetPrivateConversation(
                supportRequest.getRequester().getId(),
                assignment.getVolunteer().getId(),
                currentUser.getId());

        validateCanReviewAssignment(currentUser, supportRequest);

        if (assignment.getStatus() != VolunteerAssignmentStatus.PENDING) {
            throw new BadRequestException("Only pending assignment can be approved");
        }

        assignment.setStatus(VolunteerAssignmentStatus.ACCEPTED);
        assignment.setReviewedBy(currentUser);
        assignment.setReviewedAt(DateTimeUtils.now());
        assignment.setRejectionReason(null);
        assignment.setConversation(conversation);

        if (supportRequest.getStatus() == SupportRequestStatus.APPROVED) {
            supportRequest.setStatus(SupportRequestStatus.IN_PROGRESS);
            supportRequestRepository.save(supportRequest);
        }

        VolunteerAssignment savedAssignment = volunteerAssignmentRepository.save(assignment);

        notificationService.createNotification(
                assignment.getVolunteer().getId(),
                "Your volunteer request has been approved: " + supportRequest.getTitle(),
                "VOLUNTEER_ASSIGNMENT",
                supportRequest.getId(),
                "/conversations/" + conversation.getId());

        return volunteerAssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public VolunteerAssignmentResponse rejectVolunteer(
            UUID currentUserId,
            UUID supportRequestId,
            UUID volunteerId,
            RejectVolunteerAssignmentRequest request) {

        User currentUser = getUserById(currentUserId);
        SupportRequest supportRequest = getSupportRequestById(supportRequestId);
        VolunteerAssignment assignment = getAssignment(supportRequestId, volunteerId);

        validateCanReviewAssignment(currentUser, supportRequest);

        if (assignment.getStatus() != VolunteerAssignmentStatus.PENDING) {
            throw new BadRequestException("Only pending assignment can be rejected");
        }

        assignment.setStatus(VolunteerAssignmentStatus.REJECTED);
        assignment.setReviewedBy(currentUser);
        assignment.setReviewedAt(DateTimeUtils.now());
        assignment.setRejectionReason(normalizeRequired(
                request.getRejectionReason(),
                "Rejection reason is required"));

        VolunteerAssignment savedAssignment = volunteerAssignmentRepository.save(assignment);

        notificationService.createNotification(
                assignment.getVolunteer().getId(),
                "Your volunteer request has been rejected: " + supportRequest.getTitle(),
                "VOLUNTEER_ASSIGNMENT",
                supportRequest.getId(),
                "/support-requests/" + supportRequest.getId());

        return volunteerAssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public VolunteerAssignmentResponse cancelMyAssignment(UUID volunteerId, UUID supportRequestId) {
        User volunteer = getUserById(volunteerId);
        validateVolunteerRole(volunteer);

        SupportRequest supportRequest = getSupportRequestById(supportRequestId);
        VolunteerAssignment assignment = getAssignment(supportRequestId, volunteerId);

        if (assignment.getStatus() != VolunteerAssignmentStatus.PENDING
                && assignment.getStatus() != VolunteerAssignmentStatus.ACCEPTED) {
            throw new BadRequestException("Only pending or accepted assignment can be cancelled");
        }

        assignment.setStatus(VolunteerAssignmentStatus.CANCELLED);

        VolunteerAssignment savedAssignment = volunteerAssignmentRepository.save(assignment);

        long acceptedCount = volunteerAssignmentRepository.countBySupportRequestAndStatus(
                supportRequest,
                VolunteerAssignmentStatus.ACCEPTED);

        if (acceptedCount == 0 && supportRequest.getStatus() == SupportRequestStatus.IN_PROGRESS) {
            supportRequest.setStatus(SupportRequestStatus.APPROVED);
            supportRequestRepository.save(supportRequest);
        }

        return volunteerAssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public VolunteerAssignmentResponse completeMyAssignment(UUID volunteerId, UUID supportRequestId) {
        User volunteer = getUserById(volunteerId);
        validateVolunteerRole(volunteer);

        SupportRequest supportRequest = getSupportRequestById(supportRequestId);
        VolunteerAssignment assignment = getAssignment(supportRequestId, volunteerId);

        if (assignment.getStatus() != VolunteerAssignmentStatus.ACCEPTED) {
            throw new BadRequestException("Only accepted assignment can be completed");
        }

        assignment.setStatus(VolunteerAssignmentStatus.COMPLETED);

        if (supportRequest.getStatus() == SupportRequestStatus.IN_PROGRESS) {
            supportRequest.setStatus(SupportRequestStatus.COMPLETED);
            supportRequestRepository.save(supportRequest);
        }

        VolunteerAssignment savedAssignment = volunteerAssignmentRepository.save(assignment);

        return volunteerAssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    public List<VolunteerAssignmentResponse> getMyAssignments(UUID volunteerId) {
        User volunteer = getUserById(volunteerId);
        validateVolunteerRole(volunteer);

        return volunteerAssignmentRepository.findAllByVolunteerOrderByAssignedAtDesc(volunteer)
                .stream()
                .map(volunteerAssignmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<VolunteerAssignmentResponse> getAssignmentsBySupportRequest(UUID currentUserId, UUID supportRequestId) {
        User currentUser = getUserById(currentUserId);
        SupportRequest supportRequest = getSupportRequestById(supportRequestId);

        validateCanViewAssignments(currentUser, supportRequest);

        return volunteerAssignmentRepository.findAllBySupportRequestOrderByAssignedAtDesc(supportRequest)
                .stream()
                .map(volunteerAssignmentMapper::toResponse)
                .toList();
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private SupportRequest getSupportRequestById(UUID supportRequestId) {
        return supportRequestRepository.findById(Objects.requireNonNull(supportRequestId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Support request not found with id: " + supportRequestId));
    }

    private VolunteerAssignment getAssignment(UUID supportRequestId, UUID volunteerId) {
        VolunteerAssignmentId id = new VolunteerAssignmentId(
                Objects.requireNonNull(supportRequestId),
                Objects.requireNonNull(volunteerId));

        return volunteerAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer assignment not found"));
    }

    private void validateVolunteerRole(User user) {
        if (user.getRole() != UserRole.VOLUNTEER) {
            throw new ForbiddenException("Only volunteer can perform this action");
        }
    }

    private void validateCanReviewAssignment(User currentUser, SupportRequest supportRequest) {
        boolean isRequester = supportRequest.getRequester().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isCollaborator = currentUser.getRole() == UserRole.COLLABORATOR;

        if (!isRequester && !isAdmin && !isCollaborator) {
            throw new ForbiddenException("You do not have permission to review this assignment");
        }
    }

    private void validateCanViewAssignments(User currentUser, SupportRequest supportRequest) {
        boolean isRequester = supportRequest.getRequester().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isCollaborator = currentUser.getRole() == UserRole.COLLABORATOR;

        if (!isRequester && !isAdmin && !isCollaborator) {
            throw new ForbiddenException("You do not have permission to view assignments of this support request");
        }
    }

    private String normalizeRequired(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }
}