package com.helphub.backend.modules.communityfund.dto.request;

import com.helphub.backend.common.enums.CommunityFundMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommunityFundMemberRoleRequest {

    @NotNull(message = "Role is required")
    private CommunityFundMemberRole role;
}