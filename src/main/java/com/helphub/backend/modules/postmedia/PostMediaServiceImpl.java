package com.helphub.backend.modules.postmedia;

import com.helphub.backend.common.enums.PostStatus;
import com.helphub.backend.common.enums.PostVisibility;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.postmedia.dto.request.AttachMediaToPostRequest;
import com.helphub.backend.modules.postmedia.dto.response.PostMediaResponse;
import com.helphub.backend.persistence.entity.Media;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.PostMedia;
import com.helphub.backend.persistence.entity.PostMediaId;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.MediaRepository;
import com.helphub.backend.persistence.repository.PostMediaRepository;
import com.helphub.backend.persistence.repository.PostRepository;
import com.helphub.backend.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostMediaServiceImpl implements PostMediaService {

    private final PostMediaRepository postMediaRepository;
    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final PostMediaMapper postMediaMapper;

    @Override
    public PostMediaResponse attachMediaToPost(UUID currentUserId, UUID postId, AttachMediaToPostRequest request) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);
        Media media = getMediaByIdOrThrow(request.getMediaId());

        validateOwnership(currentUser, post);

        if (!isOwnerOrAdmin(currentUser, media)) {
            throw new ForbiddenException("You do not have permission to attach this media");
        }

        if (postMediaRepository.existsByPostAndMedia(post, media)) {
            throw new BadRequestException("This media is already attached to the post");
        }

        PostMedia postMedia = PostMedia.builder()
                .id(new PostMediaId(post.getId(), media.getId()))
                .post(post)
                .media(media)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        PostMedia savedPostMedia = postMediaRepository.save(Objects.requireNonNull(postMedia));
        return postMediaMapper.toResponse(savedPostMedia);
    }

    @Override
    public List<PostMediaResponse> getMediaByPost(UUID currentUserId, UUID postId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        if (!Boolean.TRUE.equals(post.getIsActive())) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        if (!canViewPost(currentUser, post)) {
            throw new ForbiddenException("You do not have permission to view media of this post");
        }

        if (post.getStatus() != PostStatus.ACTIVE && !post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("This post is not available for viewing");
        }

        return postMediaRepository.findAllByPostOrderByDisplayOrderAscCreatedAtAsc(post)
                .stream()
                .map(postMediaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeMediaFromPost(UUID currentUserId, UUID postId, UUID mediaId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);
        Media media = getMediaByIdOrThrow(mediaId);

        validateOwnership(currentUser, post);

        if (!postMediaRepository.existsByPostAndMedia(post, media)) {
            throw new ResourceNotFoundException("Post media relationship not found");
        }

        postMediaRepository.deleteByPostAndMedia(post, media);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Post getPostByIdOrThrow(UUID postId) {
        return postRepository.findById(Objects.requireNonNull(postId))
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private Media getMediaByIdOrThrow(UUID mediaId) {
        return mediaRepository.findById(Objects.requireNonNull(mediaId))
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + mediaId));
    }

    private void validateOwnership(User currentUser, Post post) {
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only modify media of your own post");
        }
    }

    private boolean isOwnerOrAdmin(User currentUser, Media media) {
        return media.getUploadedBy().getId().equals(currentUser.getId())
                || currentUser.getRole() == UserRole.ADMIN;
    }

    private boolean canViewPost(User currentUser, Post post) {
        if (post.getAuthor().getId().equals(currentUser.getId())) {
            return true;
        }

        if (post.getVisibility() == PostVisibility.PUBLIC) {
            return true;
        }

        return post.getVisibility() == PostVisibility.VOLUNTEERS_ONLY
                && (currentUser.getRole() == UserRole.VOLUNTEER
                        || currentUser.getRole() == UserRole.ADMIN
                        || currentUser.getRole() == UserRole.COLLABORATOR);
    }
}
