package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.Conversation;
import com.helphub.backend.persistence.entity.ConversationMember;
import com.helphub.backend.persistence.entity.ConversationMemberId;
import com.helphub.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, ConversationMemberId> {

    boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);

    Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);

    List<ConversationMember> findAllByConversationId(UUID conversationId);

    long countByConversationId(UUID conversationId);

    void deleteByConversationAndUser(Conversation conversation, User user);
}