package com.helphub.backend.modules.communityfund.dto.request;

import com.helphub.backend.common.enums.CommunityFundMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddCommunityFundMemberRequest {

    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Role is required")
    private CommunityFundMemberRole role;
}