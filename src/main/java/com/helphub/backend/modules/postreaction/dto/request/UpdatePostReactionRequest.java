package com.helphub.backend.modules.postreaction.dto.request;

import com.helphub.backend.common.enums.PostReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostReactionRequest {

    @NotNull(message = "Reaction type is required")
    private PostReactionType type;
}
