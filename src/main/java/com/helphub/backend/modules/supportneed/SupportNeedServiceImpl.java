package com.helphub.backend.modules.supportneed;

import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.common.enums.VolunteerAssignmentStatus;
import com.helphub.backend.modules.supportneed.dto.request.CreateSupportNeedContributionRequest;
import com.helphub.backend.modules.supportneed.dto.request.CreateSupportNeedRequest;
import com.helphub.backend.modules.supportneed.dto.request.UpdateSupportNeedRequest;
import com.helphub.backend.modules.supportneed.dto.response.SupportNeedContributionResponse;
import com.helphub.backend.modules.supportneed.dto.response.SupportNeedResponse;
import com.helphub.backend.persistence.entity.SupportNeed;
import com.helphub.backend.persistence.entity.SupportNeedContribution;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.SupportNeedContributionRepository;
import com.helphub.backend.persistence.repository.SupportNeedRepository;
import com.helphub.backend.persistence.repository.SupportRequestRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import com.helphub.backend.persistence.repository.VolunteerAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportNeedServiceImpl implements SupportNeedService {

    private final SupportNeedRepository supportNeedRepository;
    private final SupportNeedContributionRepository supportNeedContributionRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final VolunteerAssignmentRepository volunteerAssignmentRepository;
    private final SupportNeedMapper supportNeedMapper;

    @Override
    @Transactional
    public SupportNeedResponse createSupportNeed(
            UUID requesterId,
            UUID supportRequestId,
            CreateSupportNeedRequest request) {

        User requester = getUserById(requesterId);
        validateRequesterRole(requester);

        SupportRequest supportRequest = getSupportRequestByIdOrThrow(supportRequestId);
        validateSupportRequestOwnership(requester, supportRequest);
        validateSupportRequestCanModifyNeeds(supportRequest);

        String needName = normalizeRequired(request.getNeedName(), "Need name is required");

        if (supportNeedRepository.existsBySupportRequestAndNeedNameIgnoreCase(supportRequest, needName)) {
            throw new BadRequestException("Support need already exists in this support request");
        }

        SupportNeed supportNeed = SupportNeed.builder()
                .supportRequest(supportRequest)
                .supportType(Objects.requireNonNull(request.getSupportType()))
                .needName(needName)
                .unit(Objects.requireNonNull(request.getUnit()))
                .requiredQuantity(validatePositiveQuantity(request.getRequiredQuantity(), "Required quantity"))
                .receivedQuantity(BigDecimal.ZERO)
                .build();

        SupportNeed savedSupportNeed = supportNeedRepository.save(Objects.requireNonNull(supportNeed));
        return supportNeedMapper.toResponse(savedSupportNeed);
    }

    @Override
    public List<SupportNeedResponse> getSupportNeedsBySupportRequest(UUID supportRequestId) {
        SupportRequest supportRequest = getSupportRequestByIdOrThrow(supportRequestId);

        return supportNeedRepository.findAllBySupportRequestOrderByCreatedAtDesc(supportRequest)
                .stream()
                .map(supportNeedMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SupportNeedResponse updateSupportNeed(
            UUID requesterId,
            UUID supportNeedId,
            UpdateSupportNeedRequest request) {

        User requester = getUserById(requesterId);
        validateRequesterRole(requester);

        SupportNeed supportNeed = getSupportNeedByIdOrThrow(supportNeedId);
        SupportRequest supportRequest = supportNeed.getSupportRequest();

        validateSupportRequestOwnership(requester, supportRequest);
        validateSupportRequestCanModifyNeeds(supportRequest);

        BigDecimal requiredQuantity = validatePositiveQuantity(request.getRequiredQuantity(), "Required quantity");

        if (requiredQuantity.compareTo(supportNeed.getReceivedQuantity()) < 0) {
            throw new BadRequestException("Required quantity cannot be less than received quantity");
        }

        supportNeed.setSupportType(Objects.requireNonNull(request.getSupportType()));
        supportNeed.setNeedName(normalizeRequired(request.getNeedName(), "Need name is required"));
        supportNeed.setUnit(Objects.requireNonNull(request.getUnit()));
        supportNeed.setRequiredQuantity(requiredQuantity);

        SupportNeed savedSupportNeed = supportNeedRepository.save(supportNeed);
        return supportNeedMapper.toResponse(savedSupportNeed);
    }

    @Override
    @Transactional
    public void deleteSupportNeed(UUID requesterId, UUID supportNeedId) {
        User requester = getUserById(requesterId);
        validateRequesterRole(requester);

        SupportNeed supportNeed = getSupportNeedByIdOrThrow(supportNeedId);
        SupportRequest supportRequest = supportNeed.getSupportRequest();

        validateSupportRequestOwnership(requester, supportRequest);
        validateSupportRequestCanModifyNeeds(supportRequest);

        if (supportNeed.getReceivedQuantity().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Cannot delete support need that already has contributions");
        }

        supportNeedRepository.delete(supportNeed);
    }

    @Override
    @Transactional
    public SupportNeedContributionResponse contributeToSupportNeed(
            UUID contributorId,
            UUID supportNeedId,
            CreateSupportNeedContributionRequest request) {

        User contributor = getUserById(contributorId);

        SupportNeed supportNeed = getSupportNeedByIdOrThrow(supportNeedId);
        SupportRequest supportRequest = supportNeed.getSupportRequest();

        validateContributorCanContribute(contributor, supportRequest);
        validateSupportRequestCanReceiveContributions(supportRequest);

        if (supportRequest.getRequester().getId().equals(contributor.getId())) {
            throw new ForbiddenException("Requester cannot contribute to their own support need");
        }

        BigDecimal quantity = validatePositiveQuantity(request.getQuantity(), "Contribution quantity");

        BigDecimal newReceivedQuantity = supportNeed.getReceivedQuantity().add(quantity);

        if (newReceivedQuantity.compareTo(supportNeed.getRequiredQuantity()) > 0) {
            BigDecimal remainingQuantity = supportNeed.getRequiredQuantity()
                    .subtract(supportNeed.getReceivedQuantity());
            throw new BadRequestException(
                    "Contribution quantity exceeds remaining quantity. Remaining: " + remainingQuantity);
        }

        SupportNeedContribution contribution = SupportNeedContribution.builder()
                .supportNeed(supportNeed)
                .contributor(contributor)
                .quantity(quantity)
                .note(normalizeNullable(request.getNote()))
                .build();

        SupportNeedContribution savedContribution = supportNeedContributionRepository
                .save(Objects.requireNonNull(contribution));

        supportNeed.setReceivedQuantity(newReceivedQuantity);
        supportNeedRepository.save(supportNeed);

        updateSupportRequestStatusAfterContribution(supportRequest);

        return supportNeedMapper.toContributionResponse(savedContribution);
    }

    @Override
    public List<SupportNeedContributionResponse> getContributionsBySupportNeed(UUID supportNeedId) {
        SupportNeed supportNeed = getSupportNeedByIdOrThrow(supportNeedId);

        return supportNeedContributionRepository.findAllBySupportNeedOrderByCreatedAtDesc(supportNeed)
                .stream()
                .map(supportNeedMapper::toContributionResponse)
                .toList();
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private SupportRequest getSupportRequestByIdOrThrow(UUID supportRequestId) {
        return supportRequestRepository.findById(Objects.requireNonNull(supportRequestId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Support request not found with id: " + supportRequestId));
    }

    private SupportNeed getSupportNeedByIdOrThrow(UUID supportNeedId) {
        return supportNeedRepository.findById(Objects.requireNonNull(supportNeedId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Support need not found with id: " + supportNeedId));
    }

    private void validateRequesterRole(User user) {
        if (user.getRole() != UserRole.REQUESTER) {
            throw new ForbiddenException("Only requester can manage support needs");
        }
    }

    private void validateContributorCanContribute(User contributor, SupportRequest supportRequest) {
        if (contributor.getRole() == UserRole.COLLABORATOR) {
            return;
        }

        if (contributor.getRole() == UserRole.VOLUNTEER) {
            boolean hasAcceptedAssignment = volunteerAssignmentRepository
                    .existsBySupportRequestAndVolunteerAndStatus(
                            supportRequest,
                            contributor,
                            VolunteerAssignmentStatus.ACCEPTED);

            if (!hasAcceptedAssignment) {
                throw new ForbiddenException("Volunteer must be accepted for this support request before contributing");
            }

            return;
        }

        throw new ForbiddenException("Only assigned volunteer or collaborator can contribute to support need");
    }

    private void updateSupportRequestStatusAfterContribution(
            SupportRequest supportRequest) {

        if (supportRequest.getStatus() == SupportRequestStatus.APPROVED) {
            supportRequest.setStatus(SupportRequestStatus.IN_PROGRESS);
        }

        List<SupportNeed> supportNeeds = supportNeedRepository.findAllBySupportRequestOrderByCreatedAtDesc(
                supportRequest);

        boolean allFulfilled = supportNeeds.stream()
                .allMatch(this::isSupportNeedFulfilled);

        if (allFulfilled && !supportNeeds.isEmpty()) {
            supportRequest.setStatus(SupportRequestStatus.COMPLETED);
        }

        supportRequestRepository.save(supportRequest);
    }

    private boolean isSupportNeedFulfilled(SupportNeed supportNeed) {
        return supportNeed.getReceivedQuantity()
                .compareTo(supportNeed.getRequiredQuantity()) >= 0;
    }

    private void validateSupportRequestOwnership(User requester, SupportRequest supportRequest) {
        if (!supportRequest.getRequester().getId().equals(requester.getId())) {
            throw new ForbiddenException("You can only manage needs of your own support request");
        }
    }

    private void validateSupportRequestCanModifyNeeds(SupportRequest supportRequest) {
        if (supportRequest.getStatus() != SupportRequestStatus.PENDING
                && supportRequest.getStatus() != SupportRequestStatus.APPROVED) {
            throw new BadRequestException(
                    "Support needs can only be modified when support request is pending or approved");
        }
    }

    private void validateSupportRequestCanReceiveContributions(SupportRequest supportRequest) {
        if (supportRequest.getStatus() != SupportRequestStatus.APPROVED
                && supportRequest.getStatus() != SupportRequestStatus.IN_PROGRESS) {
            throw new BadRequestException("Support request must be approved or in progress to receive contributions");
        }
    }

    private BigDecimal validatePositiveQuantity(BigDecimal quantity, String fieldName) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0");
        }
        return quantity;
    }

    private String normalizeRequired(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }

    private String normalizeNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}