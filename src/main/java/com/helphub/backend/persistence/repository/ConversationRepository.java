package com.helphub.backend.persistence.repository;

import com.helphub.backend.common.enums.ConversationType;
import com.helphub.backend.persistence.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
            SELECT DISTINCT c
            FROM Conversation c
            JOIN c.members m
            WHERE m.user.id = :userId
            ORDER BY c.createdAt DESC
            """)
    List<Conversation> findAllByMemberId(UUID userId);

    @Query("""
            SELECT DISTINCT c
            FROM Conversation c
            JOIN c.members m
            WHERE c.type = :type
            AND m.user.id = :userId
            """)
    List<Conversation> findAllByTypeAndMemberId(ConversationType type, UUID userId);
}