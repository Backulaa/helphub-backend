package com.helphub.backend.modules.postcomment;

import com.helphub.backend.common.enums.PostStatus;
import com.helphub.backend.common.enums.PostVisibility;
import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.postcomment.dto.request.CreatePostCommentRequest;
import com.helphub.backend.modules.postcomment.dto.request.UpdatePostCommentRequest;
import com.helphub.backend.modules.postcomment.dto.response.PostCommentResponse;
import com.helphub.backend.persistence.entity.Post;
import com.helphub.backend.persistence.entity.PostComment;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.PostCommentRepository;
import com.helphub.backend.persistence.repository.PostRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCommentMapper postCommentMapper;

    @Override
    public PostCommentResponse createComment(UUID currentUserId, UUID postId, CreatePostCommentRequest request) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        PostComment parentComment = null;

        if (request.getParentCommentId() != null) {
            parentComment = getCommentByIdOrThrow(request.getParentCommentId());

            if (!parentComment.getPost().getId().equals(post.getId())) {
                throw new BadRequestException("Parent comment does not belong to this post");
            }

            if (parentComment.getParentComment() != null) {
                throw new BadRequestException("Replying to a reply is not supported");
            }
        }

        validatePostAvailableForViewing(currentUser, post);

        PostComment comment = PostComment.builder()
                .post(post)
                .user(currentUser)
                .parentComment(parentComment)
                .content(normalizeRequired(request.getContent(), "Comment content is required"))
                .build();

        PostComment savedComment = postCommentRepository.save(Objects.requireNonNull(comment));
        return postCommentMapper.toResponse(savedComment);
    }

    @Override
    public List<PostCommentResponse> getCommentsByPost(UUID currentUserId, UUID postId) {
        User currentUser = getUserById(currentUserId);
        Post post = getPostByIdOrThrow(postId);

        validatePostAvailableForViewing(currentUser, post);

        return postCommentRepository.findAllByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(postCommentMapper::toResponse)
                .toList();
    }

    @Override
    public List<PostCommentResponse> getRepliesByComment(UUID currentUserId, UUID commentId) {
        User currentUser = getUserById(currentUserId);
        PostComment parentComment = getCommentByIdOrThrow(commentId);

        validatePostAvailableForViewing(currentUser, parentComment.getPost());

        if (parentComment.getParentComment() != null) {
            throw new BadRequestException("Replies can only be fetched for root comments");
        }

        return postCommentRepository.findAllByParentCommentOrderByCreatedAtAsc(parentComment)
                .stream()
                .map(postCommentMapper::toResponse)
                .toList();
    }

    @Override
    public PostCommentResponse updateMyComment(UUID currentUserId, UUID commentId,
            UpdatePostCommentRequest request) {
        User currentUser = getUserById(currentUserId);
        PostComment comment = getCommentByIdOrThrow(commentId);

        validateCommentOwnership(currentUser, comment);

        comment.setContent(normalizeRequired(request.getContent(), "Comment content is required"));

        PostComment savedComment = postCommentRepository.save(Objects.requireNonNull(comment));
        return postCommentMapper.toResponse(savedComment);
    }

    @Override
    public void deleteMyComment(UUID currentUserId, UUID commentId) {
        User currentUser = getUserById(currentUserId);
        PostComment comment = getCommentByIdOrThrow(commentId);

        validateCanDeleteComment(currentUser, comment);

        postCommentRepository.delete(Objects.requireNonNull(comment));
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Post getPostByIdOrThrow(UUID postId) {
        return postRepository.findById(Objects.requireNonNull(postId))
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private PostComment getCommentByIdOrThrow(UUID commentId) {
        return postCommentRepository.findById(Objects.requireNonNull(commentId))
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
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

    private void validateCommentOwnership(User currentUser, PostComment comment) {
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only update your own comment");
        }
    }

    private void validateCanDeleteComment(User currentUser, PostComment comment) {
        boolean isCommentOwner = comment.getUser().getId().equals(currentUser.getId());
        boolean isPostOwner = comment.getPost().getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isCommentOwner && !isPostOwner && !isAdmin) {
            throw new ForbiddenException("You do not have permission to delete this comment");
        }
    }

    private String normalizeRequired(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }
}
