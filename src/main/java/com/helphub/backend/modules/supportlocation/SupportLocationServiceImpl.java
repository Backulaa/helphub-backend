package com.helphub.backend.modules.supportlocation;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.supportlocation.dto.request.CreateSupportLocationRequest;
import com.helphub.backend.modules.supportlocation.dto.request.UpdateSupportLocationRequest;
import com.helphub.backend.modules.supportlocation.dto.request.UpdateSupportLocationStatusRequest;
import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationDetailResponse;
import com.helphub.backend.modules.supportlocation.dto.response.SupportLocationSummaryResponse;
import com.helphub.backend.persistence.entity.SupportLocation;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.SupportLocationRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportLocationServiceImpl implements SupportLocationService {

    private final SupportLocationRepository supportLocationRepository;
    private final UserRepository userRepository;
    private final SupportLocationMapper supportLocationMapper;

    @Override
    public SupportLocationDetailResponse createSupportLocation(
            UUID creatorId,
            CreateSupportLocationRequest request) {

        User creator = getUserById(creatorId);
        validateManagerRole(creator);

        SupportLocation supportLocation = SupportLocation.builder()
                .name(normalizeRequired(request.getName(), "Name is required"))
                .description(normalizeRequired(request.getDescription(), "Description is required"))
                .latitude(validateLatitude(request.getLatitude()))
                .longitude(validateLongitude(request.getLongitude()))
                .address(normalizeRequired(request.getAddress(), "Address is required"))
                .contactPhone(normalizeNullable(request.getContactPhone()))
                .createdBy(creator)
                .bankName(normalizeNullable(request.getBankName()))
                .bankAccountNumber(normalizeNullable(request.getBankAccountNumber()))
                .isActive(true)
                .build();

        SupportLocation savedSupportLocation = supportLocationRepository.save(Objects.requireNonNull(supportLocation));

        return supportLocationMapper.toDetailResponse(savedSupportLocation);
    }

    @Override
    public List<SupportLocationSummaryResponse> getAllSupportLocations(Boolean activeOnly) {

        List<SupportLocation> supportLocations;

        if (Boolean.TRUE.equals(activeOnly)) {
            supportLocations = supportLocationRepository
                    .findAllByIsActiveTrueOrderByCreatedAtDesc();
        } else {
            supportLocations = supportLocationRepository
                    .findAllByOrderByCreatedAtDesc();
        }

        return supportLocations.stream()
                .map(supportLocationMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<SupportLocationSummaryResponse> getMyCreatedSupportLocations(UUID creatorId) {

        User creator = getUserById(creatorId);
        validateManagerRole(creator);

        return supportLocationRepository
                .findAllByCreatedByOrderByCreatedAtDesc(creator)
                .stream()
                .map(supportLocationMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public SupportLocationDetailResponse getSupportLocationById(UUID id) {

        SupportLocation supportLocation = getSupportLocationByIdOrThrow(id);

        return supportLocationMapper.toDetailResponse(supportLocation);
    }

    @Override
    public SupportLocationDetailResponse updateSupportLocation(
            UUID currentUserId,
            UUID supportLocationId,
            UpdateSupportLocationRequest request) {

        User currentUser = getUserById(currentUserId);
        validateManagerRole(currentUser);

        SupportLocation supportLocation = getSupportLocationByIdOrThrow(supportLocationId);

        supportLocation.setName(
                normalizeRequired(request.getName(), "Name is required"));

        supportLocation.setDescription(
                normalizeRequired(request.getDescription(), "Description is required"));

        supportLocation.setLatitude(
                validateLatitude(request.getLatitude()));

        supportLocation.setLongitude(
                validateLongitude(request.getLongitude()));

        supportLocation.setAddress(
                normalizeRequired(request.getAddress(), "Address is required"));

        supportLocation.setContactPhone(
                normalizeNullable(request.getContactPhone()));

        supportLocation.setBankName(
                normalizeNullable(request.getBankName()));

        supportLocation.setBankAccountNumber(
                normalizeNullable(request.getBankAccountNumber()));

        SupportLocation savedSupportLocation = supportLocationRepository.save(supportLocation);

        return supportLocationMapper.toDetailResponse(savedSupportLocation);
    }

    @Override
    public SupportLocationDetailResponse updateSupportLocationStatus(
            UUID currentUserId,
            UUID supportLocationId,
            UpdateSupportLocationStatusRequest request) {

        User currentUser = getUserById(currentUserId);
        validateManagerRole(currentUser);

        SupportLocation supportLocation = getSupportLocationByIdOrThrow(supportLocationId);

        supportLocation.setIsActive(
                Objects.requireNonNull(request.getIsActive()));

        SupportLocation savedSupportLocation = supportLocationRepository.save(supportLocation);

        return supportLocationMapper.toDetailResponse(savedSupportLocation);
    }

    private User getUserById(UUID userId) {

        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
    }

    private SupportLocation getSupportLocationByIdOrThrow(UUID supportLocationId) {

        return supportLocationRepository
                .findById(Objects.requireNonNull(supportLocationId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Support location not found with id: "
                                + supportLocationId));
    }

    private void validateManagerRole(User user) {

        if (user.getRole() != UserRole.ADMIN
                && user.getRole() != UserRole.COLLABORATOR) {

            throw new ForbiddenException(
                    "Only admin or collaborator can manage support locations");
        }
    }

    private Double validateLatitude(Double latitude) {

        if (latitude == null) {
            throw new BadRequestException("Latitude is required");
        }

        if (latitude < -90 || latitude > 90) {
            throw new BadRequestException(
                    "Latitude must be between -90 and 90");
        }

        return latitude;
    }

    private Double validateLongitude(Double longitude) {

        if (longitude == null) {
            throw new BadRequestException("Longitude is required");
        }

        if (longitude < -180 || longitude > 180) {
            throw new BadRequestException(
                    "Longitude must be between -180 and 180");
        }

        return longitude;
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