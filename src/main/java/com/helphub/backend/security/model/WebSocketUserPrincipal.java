package com.helphub.backend.security.model;

import java.security.Principal;
import java.util.UUID;

public class WebSocketUserPrincipal implements Principal {

    private final UUID userId;
    private final String email;

    public WebSocketUserPrincipal(UUID userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String getName() {
        return userId.toString();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}