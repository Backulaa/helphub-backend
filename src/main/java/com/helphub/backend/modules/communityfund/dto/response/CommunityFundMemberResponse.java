package com.helphub.backend.modules.communityfund.dto.response;

import com.helphub.backend.common.enums.CommunityFundMemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CommunityFundMemberResponse {
    private UUID fundId;
    private UUID userId;
    private String userName;
    private String userEmail;
    private CommunityFundMemberRole role;
    private Boolean isActive;
    private LocalDateTime joinedAt;
}