package com.helphub.backend.modules.media;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.media.dto.request.CreateMediaRequest;
import com.helphub.backend.modules.media.dto.request.UpdateMediaRequest;
import com.helphub.backend.modules.media.dto.response.MediaDetailResponse;
import com.helphub.backend.modules.media.dto.response.MediaSummaryResponse;
import com.helphub.backend.persistence.entity.Media;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.MediaRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final MediaMapper mediaMapper;

    @Override
    public MediaDetailResponse createMedia(UUID currentUserId, CreateMediaRequest request) {
        User currentUser = getUserById(currentUserId);

        validateMimeType(request.getMimeType());
        validateFileSize(request.getFileSize());

        Media media = Media.builder()
                .fileName(normalizeRequired(request.getFileName(), "File name is required"))
                .fileUrl(normalizeRequired(request.getFileUrl(), "File URL is required"))
                .fileType(request.getFileType())
                .mimeType(normalizeRequired(request.getMimeType(), "MIME type is required"))
                .fileSize(request.getFileSize())
                .uploadedBy(currentUser)
                .altText(normalizeNullable(request.getAltText()))
                .isPublic(request.getIsPublic())
                .build();

        Media savedMedia = mediaRepository.save(Objects.requireNonNull(media));
        return mediaMapper.toDetailResponse(savedMedia);
    }

    @Override
    public MediaDetailResponse getMediaById(UUID currentUserId, UUID mediaId) {
        User currentUser = getUserById(currentUserId);
        Media media = getMediaByIdOrThrow(mediaId);

        if (!canAccessMedia(currentUser, media)) {
            throw new ForbiddenException("You do not have permission to access this media");
        }

        return mediaMapper.toDetailResponse(media);
    }

    @Override
    public List<MediaSummaryResponse> getMyMedia(UUID currentUserId) {
        User currentUser = getUserById(currentUserId);

        return mediaRepository.findAllByUploadedByOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(mediaMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public MediaDetailResponse updateMedia(UUID userId, UUID mediaId, UpdateMediaRequest request) {
        Media media = getMediaByIdOrThrow(mediaId);
        User user = getUserById(userId);

        if (!isOwnerOrAdmin(user, media)) {
            throw new ForbiddenException("You do not have permission to update this media");
        }

        boolean hasUpdate = false;

        if (request.getAltText() != null) {
            media.setAltText(request.getAltText().trim());
            hasUpdate = true;
        }

        if (request.getIsPublic() != null) {
            media.setIsPublic(request.getIsPublic());
            hasUpdate = true;
        }

        if (!hasUpdate) {
            throw new BadRequestException("At least one field must be provided for update");
        }

        return mediaMapper.toDetailResponse(mediaRepository.save(Objects.requireNonNull(media)));
    }

    @Override
    public void deleteMedia(UUID currentUserId, UUID mediaId) {
        User currentUser = getUserById(currentUserId);
        Media media = getMediaByIdOrThrow(mediaId);

        if (!isOwnerOrAdmin(currentUser, media)) {
            throw new ForbiddenException("You do not have permission to delete this media");
        }

        mediaRepository.delete(Objects.requireNonNull(media));
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Media getMediaByIdOrThrow(UUID mediaId) {
        return mediaRepository.findById(Objects.requireNonNull(mediaId))
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + mediaId));
    }

    private boolean canAccessMedia(User currentUser, Media media) {
        if (Boolean.TRUE.equals(media.getIsPublic())) {
            return true;
        }
        return isOwnerOrAdmin(currentUser, media);
    }

    private boolean isOwnerOrAdmin(User currentUser, Media media) {
        return media.getUploadedBy().getId().equals(currentUser.getId())
                || currentUser.getRole() == UserRole.ADMIN;
    }

    private void validateMimeType(String mimeType) {
        if (!StringUtils.hasText(mimeType)) {
            throw new BadRequestException("MIME type is required");
        }
    }

    private void validateFileSize(Long fileSize) {
        if (fileSize == null || fileSize <= 0) {
            throw new BadRequestException("File size must be greater than 0");
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