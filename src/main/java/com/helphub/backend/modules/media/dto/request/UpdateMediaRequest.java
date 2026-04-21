package com.helphub.backend.modules.media.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMediaRequest {

    private String altText;

    private Boolean isPublic;
}
