package com.helphub.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommunityFundMemberId implements Serializable {

    @Column(name = "fund_id")
    private UUID fundId;

    @Column(name = "user_id")
    private UUID userId;
}