package com.helphub.backend.modules.postreaction;

import com.helphub.backend.common.enums.PostReactionType;
import com.helphub.backend.common.enums.PostStatus;
import com.helphub.backend.common.enums.PostVisibility;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.postreaction.dto.PostReactionMapper;
import com.helphub.backend.modules.postreaction.dto.request.CreatePostReactionRequest;
import com.helphub.backend.modules.postreaction.dto.request.UpdatePostReactionRequest;
import com.helphub.backend.modules.postreaction.dto.response.PostReactionCountResponse;
import com.helphub.backend.modules.postreaction.dto.response.PostReactionResponse;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.PostReaction;
import com.helphub.backend.persistence.entity.PostReactionId;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.PostReactionRepository;
import com.helphub.backend.persistence.repository.PostRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostReactionMapper postReactionMapper;

    @Override
    public PostReactionResponse createReaction(UUID currentUserId, UUID postId, CreatePostReactionRequest request) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validatePostAvailableForViewing(currentUser, post);

        if (postReactionRepository.existsByPostAndUser(post, currentUser)) {
            throw new BadRequestException("You have already reacted to this post. Use update to change reaction type");
        }

        PostReaction reaction = PostReaction.builder()
                .id(new PostReactionId(post.getId(), currentUser.getId()))
                .post(post)
                .user(currentUser)
                .type(request.getType())
                .build();

        PostReaction savedReaction = postReactionRepository.save(Objects.requireNonNull(reaction));
        return postReactionMapper.toResponse(savedReaction);
    }

    @Override
    public PostReactionResponse updateReaction(UUID currentUserId, UUID postId, UpdatePostReactionRequest request) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validatePostAvailableForViewing(currentUser, post);

        PostReaction reaction = postReactionRepository.findByPostAndUser(post, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found for this post"));

        if (reaction.getType() == request.getType()) {
            throw new BadRequestException("Reaction already has this type");
        }

        reaction.setType(request.getType());
        reaction.setUpdatedAt(LocalDateTime.now());

        PostReaction savedReaction = postReactionRepository.save(reaction);
        return postReactionMapper.toResponse(savedReaction);
    }

    @Override
    @Transactional
    public void deleteReaction(UUID currentUserId, UUID postId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validatePostAvailableForViewing(currentUser, post);

        if (!postReactionRepository.existsByPostAndUser(post, currentUser)) {
            throw new ResourceNotFoundException("Reaction not found for this post");
        }

        postReactionRepository.deleteByPostAndUser(post, currentUser);
    }

    @Override
    public PostReactionResponse getMyReaction(UUID currentUserId, UUID postId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validatePostAvailableForViewing(currentUser, post);

        PostReaction reaction = postReactionRepository.findByPostAndUser(post, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found for this post"));

        return postReactionMapper.toResponse(reaction);
    }

    @Override
    public PostReactionCountResponse getReactionCount(UUID currentUserId, UUID postId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validatePostAvailableForViewing(currentUser, post);

        Map<String, Long> countByType = new HashMap<>();

        for (PostReactionType type : PostReactionType.values()) {
            countByType.put(type.name(), postReactionRepository.countByPostAndType(post, type));
        }

        long totalCount = postReactionRepository.countByPost(post);

        return PostReactionCountResponse.builder()
                .postId(post.getId())
                .totalCount(totalCount)
                .countByType(countByType)
                .build();
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Post getPostByIdOrThrow(UUID postId) {
        return postRepository.findById(Objects.requireNonNull(postId))
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private void validatePostAvailableForViewing(User currentUser, Post post) {
        if (!Boolean.TRUE.equals(post.getIsActive())) {
            throw new ResourceNotFoundException("Post not found with id: " + post.getId());
        }

        if (post.getStatus() != PostStatus.ACTIVE && !post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("This post is not available for viewing");
        }

        if (!canViewPost(currentUser, post)) {
            throw new ForbiddenException("You do not have permission to access this post");
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
}
