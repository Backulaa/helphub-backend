package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

        List<Message> findAllByConversationIdOrderByCreatedAtAsc(UUID conversationId);

        boolean existsByIdAndConversationId(UUID messageId, UUID conversationId);

        long countByConversationIdAndSenderIdNot(UUID conversationId, UUID senderId);

        long countByConversationIdAndSenderIdNotAndCreatedAtAfter(
                        UUID conversationId,
                        UUID senderId,
                        LocalDateTime createdAt);

        Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(UUID conversationId);
}