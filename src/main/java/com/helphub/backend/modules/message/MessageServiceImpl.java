package com.helphub.backend.modules.message;

import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.common.util.DateTimeUtils;
import com.helphub.backend.modules.message.dto.request.SendMessageRequest;
import com.helphub.backend.modules.message.dto.request.UpdateMessageRequest;
import com.helphub.backend.modules.message.dto.response.MessageResponse;
import com.helphub.backend.persistence.entity.*;
import com.helphub.backend.persistence.repository.*;
import com.helphub.backend.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final MessageMediaRepository messageMediaRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageResponse sendMessage(UUID conversationId, SendMessageRequest request) {
        User currentUser = getCurrentUser();
        Conversation conversation = findConversationById(conversationId);

        ConversationMember member = findConversationMember(conversationId, currentUser.getId());

        String normalizedContent = normalizeNullableContent(request.getContent());
        List<UUID> mediaIds = normalizeMediaIds(request.getMediaIds());

        validateMessagePayload(normalizedContent, mediaIds);

        Message message = Message.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content(normalizedContent)
                .build();

        messageRepository.save(Objects.requireNonNull(message));

        List<MessageMedia> messageMediaList = attachMediaToMessage(message, mediaIds);

        member.setLastReadMessage(message);
        conversationMemberRepository.save(member);

        conversation.setUpdatedAt(DateTimeUtils.now());
        conversationRepository.save(conversation);

        return messageMapper.toResponse(message, messageMediaList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID conversationId) {
        User currentUser = getCurrentUser();

        findConversationById(conversationId);
        validateConversationMember(conversationId, currentUser.getId());

        return messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(message -> {
                    List<MessageMedia> media = messageMediaRepository.findAllByMessageId(message.getId());
                    return messageMapper.toResponse(message, media);
                })
                .toList();
    }

    @Override
    @Transactional
    public MessageResponse updateMessage(UUID messageId, UpdateMessageRequest request) {
        User currentUser = getCurrentUser();
        Message message = findMessageById(messageId);

        validateConversationMember(message.getConversation().getId(), currentUser.getId());

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only edit your own message");
        }

        String normalizedContent = normalizeNullableContent(request.getContent());

        if (normalizedContent == null) {
            throw new BadRequestException("Updated message content is required");
        }

        message.setContent(normalizedContent);
        message.setEditedAt(DateTimeUtils.now());

        messageRepository.save(message);

        List<MessageMedia> media = messageMediaRepository.findAllByMessageId(message.getId());
        return messageMapper.toResponse(message, media);
    }

    @Override
    @Transactional
    public void markAsRead(UUID conversationId, UUID messageId) {
        User currentUser = getCurrentUser();

        findConversationById(conversationId);
        Message message = findMessageById(messageId);

        if (!message.getConversation().getId().equals(conversationId)) {
            throw new BadRequestException("Message does not belong to this conversation");
        }

        ConversationMember member = findConversationMember(conversationId, currentUser.getId());
        member.setLastReadMessage(message);

        conversationMemberRepository.save(member);
    }

    private List<MessageMedia> attachMediaToMessage(Message message, List<UUID> mediaIds) {
        if (mediaIds.isEmpty()) {
            return List.of();
        }

        List<Media> mediaList = mediaRepository.findAllById(mediaIds);

        if (mediaList.size() != mediaIds.size()) {
            throw new ResourceNotFoundException("One or more media files were not found");
        }

        List<MessageMedia> messageMediaList = mediaList.stream()
                .map(media -> MessageMedia.builder()
                        .id(new MessageMediaId(message.getId(), media.getId()))
                        .message(message)
                        .media(media)
                        .build())
                .toList();

        return messageMediaRepository.saveAll(Objects.requireNonNull(messageMediaList));
    }

    private Conversation findConversationById(UUID conversationId) {
        return conversationRepository.findById(Objects.requireNonNull(conversationId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation not found with id: " + conversationId));
    }

    private Message findMessageById(UUID messageId) {
        return messageRepository.findById(Objects.requireNonNull(messageId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Message not found with id: " + messageId));
    }

    private ConversationMember findConversationMember(UUID conversationId, UUID userId) {
        return conversationMemberRepository.findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this conversation"));
    }

    private void validateConversationMember(UUID conversationId, UUID userId) {
        boolean isMember = conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId);

        if (!isMember) {
            throw new ForbiddenException("You are not a member of this conversation");
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ForbiddenException("Unauthenticated user");
        }

        return userRepository.findByIdAndIsActiveTrue(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userDetails.getUserId()));
    }

    private String normalizeNullableContent(String content) {
        if (content == null) {
            return null;
        }

        String trimmed = content.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<UUID> normalizeMediaIds(List<UUID> mediaIds) {
        if (mediaIds == null) {
            return List.of();
        }

        return mediaIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void validateMessagePayload(String content, List<UUID> mediaIds) {
        boolean hasContent = content != null && !content.isBlank();
        boolean hasMedia = mediaIds != null && !mediaIds.isEmpty();

        if (!hasContent && !hasMedia) {
            throw new BadRequestException("Message must contain text or media");
        }
    }
}