package com.helphub.backend.modules.conversation;

import com.helphub.backend.common.enums.ConversationType;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.conversation.dto.request.AddConversationMemberRequest;
import com.helphub.backend.modules.conversation.dto.request.CreateGroupConversationRequest;
import com.helphub.backend.modules.conversation.dto.request.CreatePrivateConversationByEmailRequest;
import com.helphub.backend.modules.conversation.dto.request.CreatePrivateConversationRequest;
import com.helphub.backend.modules.conversation.dto.response.ConversationDetailResponse;
import com.helphub.backend.modules.conversation.dto.response.ConversationSummaryResponse;
import com.helphub.backend.persistence.entity.Conversation;
import com.helphub.backend.persistence.entity.ConversationMember;
import com.helphub.backend.persistence.entity.Message;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.ConversationMemberRepository;
import com.helphub.backend.persistence.repository.ConversationRepository;
import com.helphub.backend.persistence.repository.MessageRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import com.helphub.backend.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public ConversationDetailResponse createPrivateConversation(CreatePrivateConversationRequest request) {
        User currentUser = getCurrentUser();
        User receiver = findActiveUserById(request.getReceiverId());

        return createPrivateConversation(currentUser, receiver);
    }

    @Override
    @Transactional
    public ConversationDetailResponse createPrivateConversationByEmail(CreatePrivateConversationByEmailRequest request) {
        User currentUser = getCurrentUser();
        User receiver = findActiveUserByEmail(request.getReceiverEmail());

        return createPrivateConversation(currentUser, receiver);
    }

    private ConversationDetailResponse createPrivateConversation(User currentUser, User receiver) {
        if (currentUser.getId().equals(receiver.getId())) {
            throw new BadRequestException("Cannot create private conversation with yourself");
        }

        Optional<Conversation> existingConversation = findExistingPrivateConversation(
                currentUser.getId(),
                receiver.getId());

        if (existingConversation.isPresent()) {
            return conversationMapper.toDetailResponse(existingConversation.get());
        }

        Conversation conversation = Conversation.builder()
                .type(ConversationType.PRIVATE)
                .createdBy(currentUser)
                .build();

        conversationRepository.save(Objects.requireNonNull(conversation));

        addMemberToConversation(conversation, currentUser);
        addMemberToConversation(conversation, receiver);

        Conversation savedConversation = findConversationById(conversation.getId());
        return conversationMapper.toDetailResponse(savedConversation);
    }

    @Override
    @Transactional
    public ConversationDetailResponse createGroupConversation(CreateGroupConversationRequest request) {
        User currentUser = getCurrentUser();

        Set<UUID> memberIds = new HashSet<>(request.getMemberIds());
        memberIds.add(currentUser.getId());

        if (memberIds.size() < 3) {
            throw new BadRequestException("Group conversation must have at least 3 members");
        }

        Conversation conversation = Conversation.builder()
                .type(ConversationType.GROUP)
                .createdBy(currentUser)
                .build();

        conversationRepository.save(Objects.requireNonNull(conversation));

        for (UUID memberId : memberIds) {
            User member = findActiveUserById(memberId);
            addMemberToConversation(conversation, member);
        }

        Conversation savedConversation = findConversationById(conversation.getId());
        return conversationMapper.toDetailResponse(savedConversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getMyConversations() {
        User currentUser = getCurrentUser();

        return conversationRepository.findAllByMemberId(currentUser.getId())
                .stream()
                .map(conversation -> {

                    ConversationSummaryResponse response = conversationMapper.toSummaryResponse(conversation);

                    Optional<Message> lastMessageOpt = messageRepository
                            .findTopByConversationIdOrderByCreatedAtDesc(conversation.getId());

                    UUID lastMessageId = null;
                    LocalDateTime lastMessageTime = null;

                    if (lastMessageOpt.isPresent()) {
                        Message lastMessage = lastMessageOpt.get();
                        lastMessageId = lastMessage.getId();
                        lastMessageTime = lastMessage.getCreatedAt();
                    }

                    response.setLastMessageId(lastMessageId);

                    ConversationMember member = conversationMemberRepository
                            .findByConversationIdAndUserId(conversation.getId(), currentUser.getId())
                            .orElse(null);

                    LocalDateTime lastReadAt = null;

                    if (member != null && member.getLastReadMessage() != null) {
                        lastReadAt = member.getLastReadMessage().getCreatedAt();
                    }

                    long unreadCount;

                    if (lastReadAt == null) {
                        unreadCount = messageRepository.countByConversationIdAndSenderIdNot(
                                conversation.getId(),
                                currentUser.getId());
                    } else {
                        unreadCount = messageRepository.countByConversationIdAndSenderIdNotAndCreatedAtAfter(
                                conversation.getId(),
                                currentUser.getId(),
                                lastReadAt);
                    }

                    response.setUnreadCount((int) unreadCount);

                    return response;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversationById(UUID conversationId) {
        User currentUser = getCurrentUser();
        Conversation conversation = findConversationById(conversationId);

        ConversationMember member = conversationMemberRepository
                .findByConversationIdAndUserId(conversation.getId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenException("You are not a member of this conversation"));

        ConversationDetailResponse response = conversationMapper.toDetailResponse(conversation);

        UUID lastReadMessageId = null;
        LocalDateTime lastReadAt = null;

        if (member.getLastReadMessage() != null) {
            lastReadMessageId = member.getLastReadMessage().getId();
            lastReadAt = member.getLastReadMessage().getCreatedAt();
        }

        response.setMyLastReadMessageId(lastReadMessageId);

        long unreadCount;

        if (lastReadAt == null) {
            unreadCount = messageRepository.countByConversationIdAndSenderIdNot(
                    conversation.getId(),
                    currentUser.getId());
        } else {
            unreadCount = messageRepository.countByConversationIdAndSenderIdNotAndCreatedAtAfter(
                    conversation.getId(),
                    currentUser.getId(),
                    lastReadAt);
        }

        response.setUnreadCount((int) unreadCount);

        return response;
    }

    @Override
    @Transactional
    public ConversationDetailResponse addMember(UUID conversationId, AddConversationMemberRequest request) {
        User currentUser = getCurrentUser();
        Conversation conversation = findConversationById(conversationId);

        validateMember(conversation.getId(), currentUser.getId());

        if (conversation.getType() != ConversationType.GROUP) {
            throw new BadRequestException("Cannot add member to private conversation");
        }

        User newMember = findActiveUserById(request.getUserId());

        if (conversationMemberRepository.existsByConversationIdAndUserId(conversation.getId(), newMember.getId())) {
            throw new BadRequestException("User is already a member of this conversation");
        }

        addMemberToConversation(conversation, newMember);

        Conversation updatedConversation = findConversationById(conversation.getId());
        return conversationMapper.toDetailResponse(updatedConversation);
    }

    @Override
    @Transactional
    public void leaveConversation(UUID conversationId) {
        User currentUser = getCurrentUser();
        Conversation conversation = findConversationById(conversationId);

        validateMember(conversation.getId(), currentUser.getId());

        if (conversation.getType() == ConversationType.PRIVATE) {
            throw new BadRequestException("Cannot leave private conversation");
        }

        long memberCount = conversationMemberRepository.countByConversationId(conversation.getId());

        if (memberCount <= 3) {
            throw new BadRequestException("Group conversation must have at least 3 members");
        }

        conversationMemberRepository.deleteByConversationAndUser(conversation, currentUser);
    }

    private Optional<Conversation> findExistingPrivateConversation(UUID currentUserId, UUID receiverId) {
        return conversationRepository.findAllByTypeAndMemberId(ConversationType.PRIVATE, currentUserId)
                .stream()
                .filter(conversation -> conversationMemberRepository
                        .existsByConversationIdAndUserId(conversation.getId(), receiverId))
                .findFirst();
    }

    @Override
    @Transactional
    public Conversation createOrGetPrivateConversation(UUID firstUserId, UUID secondUserId, UUID createdById) {
        User firstUser = findActiveUserById(firstUserId);
        User secondUser = findActiveUserById(secondUserId);
        User createdBy = findActiveUserById(createdById);

        if (firstUser.getId().equals(secondUser.getId())) {
            throw new BadRequestException("Cannot create private conversation with yourself");
        }

        Optional<Conversation> existingConversation = findExistingPrivateConversation(
                firstUser.getId(),
                secondUser.getId());

        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        Conversation conversation = Conversation.builder()
                .type(ConversationType.PRIVATE)
                .createdBy(createdBy)
                .build();

        conversationRepository.save(Objects.requireNonNull(conversation));

        addMemberToConversation(conversation, firstUser);
        addMemberToConversation(conversation, secondUser);

        return findConversationById(conversation.getId());
    }

    private void addMemberToConversation(Conversation conversation, User user) {
        ConversationMember member = ConversationMember.builder()
                .conversation(conversation)
                .user(user)
                .build();

        conversationMemberRepository.save(Objects.requireNonNull(member));
    }

    private Conversation findConversationById(UUID conversationId) {
        return conversationRepository.findById(Objects.requireNonNull(conversationId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation not found with id: " + conversationId));
    }

    private User findActiveUserById(UUID userId) {
        return userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
    }

    private User findActiveUserByEmail(String email) {
        String normalizedEmail = Objects.requireNonNull(email).trim();

        return userRepository.findByEmailIgnoreCaseAndIsActiveTrue(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + normalizedEmail));
    }

    private void validateMember(UUID conversationId, UUID userId) {
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

        return findActiveUserById(userDetails.getUserId());
    }
}
