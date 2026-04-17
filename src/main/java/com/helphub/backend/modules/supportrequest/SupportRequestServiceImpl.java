package com.helphub.backend.modules.supportrequest;

import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.supportrequest.dto.request.AssignSupportRequestToSupportLocationRequest;
import com.helphub.backend.modules.supportrequest.dto.request.CreateSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.request.RejectSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.request.UpdateSupportRequestRequest;
import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestDetailResponse;
import com.helphub.backend.modules.supportrequest.dto.response.SupportRequestSummaryResponse;
import com.helphub.backend.persistence.entity.Category;
import com.helphub.backend.persistence.entity.SupportLocation;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.CategoryRepository;
import com.helphub.backend.persistence.repository.SupportLocationRepository;
import com.helphub.backend.persistence.repository.SupportRequestRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final SupportLocationRepository supportLocationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SupportRequestMapper supportRequestMapper;

    @Override
    public SupportRequestDetailResponse createSupportRequest(UUID requesterId, CreateSupportRequestRequest request) {
        User requester = getUserById(requesterId);
        validateRequesterRole(requester);

        Category category = getActiveCategoryById(request.getCategoryId());

        SupportRequest supportRequest = SupportRequest.builder()
                .title(normalizeRequired(request.getTitle(), "Title is required"))
                .description(normalizeRequired(request.getDescription(), "Description is required"))
                .category(category)
                .requester(requester)
                .address(normalizeNullable(request.getAddress()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(SupportRequestStatus.PENDING)
                .build();

        SupportRequest savedSupportRequest = supportRequestRepository.save(Objects.requireNonNull(supportRequest));
        return supportRequestMapper.toDetailResponse(savedSupportRequest);
    }

    @Override
    public List<SupportRequestSummaryResponse> getAllSupportRequests(SupportRequestStatus status) {
        List<SupportRequest> supportRequests = (status == null)
                ? supportRequestRepository.findAllByOrderByCreatedAtDesc()
                : supportRequestRepository.findAllByStatusOrderByCreatedAtDesc(status);

        return supportRequests.stream()
                .map(supportRequestMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<SupportRequestSummaryResponse> getMySupportRequests(UUID requesterId) {
        User requester = getUserById(requesterId);

        return supportRequestRepository.findAllByRequesterOrderByCreatedAtDesc(requester)
                .stream()
                .map(supportRequestMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public SupportRequestDetailResponse getSupportRequestById(UUID id) {
        SupportRequest supportRequest = getSupportRequestByIdOrThrow(id);
        return supportRequestMapper.toDetailResponse(supportRequest);
    }

    @Override
    public SupportRequestDetailResponse updateMySupportRequest(
            UUID requesterId,
            UUID supportRequestId,
            UpdateSupportRequestRequest request) {

        User requester = getUserById(requesterId);
        SupportRequest supportRequest = getSupportRequestByIdOrThrow(supportRequestId);

        validateOwnership(requester, supportRequest);

        if (supportRequest.getStatus() != SupportRequestStatus.PENDING) {
            throw new BadRequestException("Only pending support request can be updated");
        }

        Category category = getActiveCategoryById(request.getCategoryId());

        supportRequest.setTitle(normalizeRequired(request.getTitle(), "Title is required"));
        supportRequest.setDescription(normalizeRequired(request.getDescription(), "Description is required"));
        supportRequest.setCategory(category);
        supportRequest.setAddress(normalizeNullable(request.getAddress()));
        supportRequest.setLatitude(request.getLatitude());
        supportRequest.setLongitude(request.getLongitude());

        SupportRequest savedSupportRequest = supportRequestRepository.save(supportRequest);
        return supportRequestMapper.toDetailResponse(savedSupportRequest);
    }

    @Override
    public SupportRequestDetailResponse approveSupportRequest(UUID reviewerId, UUID supportRequestId) {
        User reviewer = getUserById(reviewerId);
        validateReviewerRole(reviewer);

        SupportRequest supportRequest = getSupportRequestByIdOrThrow(supportRequestId);

        if (supportRequest.getStatus() != SupportRequestStatus.PENDING) {
            throw new BadRequestException("Only pending support request can be approved");
        }

        supportRequest.setStatus(SupportRequestStatus.APPROVED);
        supportRequest.setReviewedBy(reviewer);
        supportRequest.setReviewedAt(LocalDateTime.now());
        supportRequest.setRejectionReason(null);

        SupportRequest savedSupportRequest = supportRequestRepository.save(supportRequest);
        return supportRequestMapper.toDetailResponse(savedSupportRequest);
    }

    @Override
    public SupportRequestDetailResponse rejectSupportRequest(
            UUID reviewerId,
            UUID supportRequestId,
            RejectSupportRequestRequest request) {

        User reviewer = getUserById(reviewerId);
        validateReviewerRole(reviewer);

        SupportRequest supportRequest = getSupportRequestByIdOrThrow(supportRequestId);

        if (supportRequest.getStatus() != SupportRequestStatus.PENDING) {
            throw new BadRequestException("Only pending support request can be rejected");
        }

        supportRequest.setStatus(SupportRequestStatus.REJECTED);
        supportRequest.setReviewedBy(reviewer);
        supportRequest.setReviewedAt(LocalDateTime.now());
        supportRequest.setRejectionReason(
                normalizeRequired(request.getRejectionReason(), "Rejection reason is required"));

        SupportRequest savedSupportRequest = supportRequestRepository.save(supportRequest);
        return supportRequestMapper.toDetailResponse(savedSupportRequest);
    }

    @Override
    public SupportRequestDetailResponse assignSupportRequestToSupportLocation(
            UUID reviewerId,
            UUID supportRequestId,
            AssignSupportRequestToSupportLocationRequest request) {

        User reviewer = getUserById(reviewerId);
        validateReviewerRole(reviewer);

        SupportRequest supportRequest = getSupportRequestByIdOrThrow(supportRequestId);

        if (supportRequest.getStatus() != SupportRequestStatus.APPROVED) {
            throw new BadRequestException("Only approved support request can be assigned to support location");
        }

        SupportLocation supportLocation = getActiveSupportLocationById(request.getSupportLocationId());

        supportRequest.setAssignedSupportLocation(supportLocation);

        SupportRequest savedSupportRequest = supportRequestRepository.save(supportRequest);
        return supportRequestMapper.toDetailResponse(savedSupportRequest);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Category getActiveCategoryById(UUID categoryId) {
        Category category = categoryRepository.findById(Objects.requireNonNull(categoryId))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (!Boolean.TRUE.equals(category.getIsActive())) {
            throw new BadRequestException("Category is inactive");
        }

        return category;
    }

    private SupportLocation getActiveSupportLocationById(UUID supportLocationId) {
        SupportLocation supportLocation = supportLocationRepository.findById(Objects.requireNonNull(supportLocationId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Support location not found with id: " + supportLocationId));

        if (!Boolean.TRUE.equals(supportLocation.getIsActive())) {
            throw new BadRequestException("Support location is inactive");
        }

        return supportLocation;
    }

    private SupportRequest getSupportRequestByIdOrThrow(UUID supportRequestId) {
        return supportRequestRepository.findById(Objects.requireNonNull(supportRequestId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Support request not found with id: " + supportRequestId));
    }

    private void validateRequesterRole(User user) {
        if (user.getRole() != UserRole.REQUESTER) {
            throw new ForbiddenException("Only requester can create support request");
        }
    }

    private void validateReviewerRole(User user) {
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.COLLABORATOR) {
            throw new ForbiddenException("Only admin or collaborator can review support request");
        }
    }

    private void validateOwnership(User requester, SupportRequest supportRequest) {
        if (!supportRequest.getRequester().getId().equals(requester.getId())) {
            throw new ForbiddenException("You can only update your own support request");
        }
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