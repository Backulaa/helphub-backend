package com.helphub.backend.modules.post;

import com.helphub.backend.common.enums.PostStatus;
import com.helphub.backend.common.enums.PostVisibility;
import com.helphub.backend.common.enums.SupportRequestStatus;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.post.dto.request.CreatePostRequest;
import com.helphub.backend.modules.post.dto.request.UpdatePostRequest;
import com.helphub.backend.modules.post.dto.response.PostDetailResponse;
import com.helphub.backend.modules.post.dto.response.PostSummaryResponse;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.SupportRequest;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.PostRepository;
import com.helphub.backend.persistence.repository.SupportRequestRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final PostMapper postMapper;

    @Override
    public PostDetailResponse createPost(UUID authorId, CreatePostRequest request) {
        User author = getUserById(authorId);
        SupportRequest supportRequest = getOptionalSupportRequestForPost(author, request.getSupportRequestId());

        Post post = Post.builder()
                .author(author)
                .supportRequest(supportRequest)
                .content(normalizeRequired(request.getContent(), "Content is required"))
                .visibility(request.getVisibility())
                .status(PostStatus.ACTIVE)
                .isActive(true)
                .build();

        Post savedPost = postRepository.save(Objects.requireNonNull(post));
        return postMapper.toDetailResponse(savedPost);
    }

    @Override
    public List<PostSummaryResponse> getAllPosts(UUID currentUserId) {
        User currentUser = getUserById(currentUserId);

        return postRepository.findAllByIsActiveTrueAndStatusOrderByCreatedAtDesc(PostStatus.ACTIVE)
                .stream()
                .filter(post -> canViewPost(currentUser, post))
                .map(postMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<PostSummaryResponse> getMyPosts(UUID currentUserId) {
        User currentUser = getUserById(currentUserId);

        return postRepository.findAllByAuthorAndIsActiveTrueOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(postMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public PostDetailResponse getPostById(UUID currentUserId, UUID postId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        if (!Boolean.TRUE.equals(post.getIsActive())) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        if (!canViewPost(currentUser, post)) {
            throw new ForbiddenException("You do not have permission to view this post");
        }

        if (post.getStatus() != PostStatus.ACTIVE && !post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("This post is not available for viewing");
        }

        return postMapper.toDetailResponse(post);
    }

    @Override
    public PostDetailResponse updateMyPost(UUID currentUserId, UUID postId, UpdatePostRequest request) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validateOwnership(currentUser, post);

        if (!Boolean.TRUE.equals(post.getIsActive())) {
            throw new BadRequestException("Cannot update inactive post");
        }

        if (post.getStatus() == PostStatus.REMOVED) {
            throw new BadRequestException("Cannot update removed post");
        }

        SupportRequest supportRequest = getOptionalSupportRequestForPost(currentUser, request.getSupportRequestId());

        post.setContent(normalizeRequired(request.getContent(), "Content is required"));
        post.setVisibility(request.getVisibility());
        post.setSupportRequest(supportRequest);

        if (post.getStatus() == PostStatus.HIDDEN || post.getStatus() == PostStatus.UNDER_REVIEW) {
            post.setStatus(PostStatus.ACTIVE);
        }

        Post savedPost = postRepository.save(post);
        return postMapper.toDetailResponse(savedPost);
    }

    @Override
    public void deleteMyPost(UUID currentUserId, UUID postId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validateOwnership(currentUser, post);

        if (!Boolean.TRUE.equals(post.getIsActive())) {
            throw new BadRequestException("Post is already inactive");
        }

        post.setIsActive(false);
        postRepository.save(post);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Post getPostByIdOrThrow(UUID postId) {
        return postRepository.findById(Objects.requireNonNull(postId))
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private SupportRequest getOptionalSupportRequestForPost(User author, UUID supportRequestId) {
        if (supportRequestId == null) {
            return null;
        }

        SupportRequest supportRequest = supportRequestRepository.findById(supportRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Support request not found with id: " + supportRequestId));

        validateSupportRequestLink(author, supportRequest);
        return supportRequest;
    }

    private void validateSupportRequestLink(User author, SupportRequest supportRequest) {
        if (supportRequest.getStatus() != SupportRequestStatus.APPROVED
                && supportRequest.getStatus() != SupportRequestStatus.IN_PROGRESS
                && supportRequest.getStatus() != SupportRequestStatus.COMPLETED) {
            throw new BadRequestException("Support request is not eligible to be shared");
        }

        switch (author.getRole()) {

            case REQUESTER:
                if (!supportRequest.getRequester().getId().equals(author.getId())) {
                    throw new ForbiddenException("You can only attach your own support request");
                }
                break;

            case VOLUNTEER:
            case ADMIN:
            case COLLABORATOR:
                break;

            default:
                throw new ForbiddenException("You are not allowed to attach support request to post");
        }
    }

    private void validateOwnership(User currentUser, Post post) {
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only modify your own post");
        }
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

    private String normalizeRequired(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }
}