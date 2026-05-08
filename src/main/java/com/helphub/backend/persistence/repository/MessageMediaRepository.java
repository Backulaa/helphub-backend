package com.helphub.backend.persistence.repository;

import com.helphub.backend.persistence.entity.MessageMedia;
import com.helphub.backend.persistence.entity.MessageMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageMediaRepository extends JpaRepository<MessageMedia, MessageMediaId> {

    @Query("""
            SELECT mm
            FROM MessageMedia mm
            JOIN FETCH mm.media
            WHERE mm.message.id = :messageId
            """)
    List<MessageMedia> findAllByMessageId(UUID messageId);

    @Query("""
            SELECT mm
            FROM MessageMedia mm
            JOIN FETCH mm.media
            WHERE mm.message.id IN :messageIds
            """)
    List<MessageMedia> findAllByMessageIds(List<UUID> messageIds);
}