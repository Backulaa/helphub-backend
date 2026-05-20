package com.helphub.backend.persistence.entity;

import com.helphub.backend.common.enums.CommunityFundMemberRole;
import com.helphub.backend.common.util.DateTimeUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_fund_members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityFundMember {

    @EmbeddedId
    private CommunityFundMemberId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("fundId")
    @JoinColumn(name = "fund_id", nullable = false)
    private CommunityFund fund;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CommunityFundMemberRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    public void prePersistCommunityFundMember() {
        if (this.joinedAt == null) {
            this.joinedAt = DateTimeUtils.now();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.role == null) {
            this.role = CommunityFundMemberRole.MEMBER;
        }
    }
}