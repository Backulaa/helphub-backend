package com.helphub.backend.modules.conversation;

import com.helphub.backend.common.enums.ConversationType;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.exception.ForbiddenException;
import com.helphub.backend.common.exception.ResourceNotFoundException;
import com.helphub.backend.modules.conversation.dto.request.AddConversationMemberRequest;
import com.helphub.backend.modules.conversation.dto.request.CreateGroupConversationRequest;
import com.helphub.backend.modules.conversation.dto.request.CreatePrivateConversationRequest;
import com.helphub.backend.modules.conversation.dto.response.ConversationDetailResponse;
import com.helphub.backend.modules.conversation.dto.response.ConversationSummaryResponse;
import com.helphub.backend.persistence.entity.Conversation;
import com.helphub.backend.persistence.entity.ConversationMember;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.ConversationMemberRepository;
import com.helphub.backend.persistence.repository.ConversationRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import com.helphub.backend.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    @Override
    @Transactional
    public ConversationDetailResponse createPrivateConversation(CreatePrivateConversationRequest request) {
        User currentUser = getCurrentUser();
        User receiver = findActiveUserById(request.getReceiverId());

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
                .map(conversationMapper::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversationById(UUID conversationId) {
        User currentUser = getCurrentUser();
        Conversation conversation = findConversationById(conversationId);

        validateMember(conversation.getId(), currentUser.getId());

        return conversationMapper.toDetailResponse(conversation);
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