package com.helphub.backend.modules.auth;

import com.helphub.backend.modules.auth.dto.request.LoginRequest;
import com.helphub.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.helphub.backend.modules.auth.dto.request.RegisterRequest;
import com.helphub.backend.modules.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
