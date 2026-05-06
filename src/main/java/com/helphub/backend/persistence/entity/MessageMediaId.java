package com.helphub.backend.persistence.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageMediaId implements Serializable {
    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "media_id")
    private UUID mediaId;
}