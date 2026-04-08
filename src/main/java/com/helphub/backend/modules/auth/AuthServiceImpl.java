package com.helphub.backend.modules.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helphub.backend.common.enums.UserRole;
import com.helphub.backend.common.exception.EmailAlreadyExistsException;
import com.helphub.backend.common.exception.UnauthorizedException;
import com.helphub.backend.common.exception.BadRequestException;
import com.helphub.backend.common.util.DateTimeUtils;
import com.helphub.backend.modules.auth.dto.request.LoginRequest;
import com.helphub.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.helphub.backend.modules.auth.dto.request.RegisterRequest;
import com.helphub.backend.modules.auth.dto.response.AuthResponse;
import com.helphub.backend.persistence.entity.RefreshToken;
import com.helphub.backend.persistence.entity.User;
import com.helphub.backend.persistence.repository.RefreshTokenRepository;
import com.helphub.backend.persistence.repository.UserRepository;
import com.helphub.backend.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
        private final UserRepository userRepository;
        private final RefreshTokenRepository refreshTokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new EmailAlreadyExistsException("Email already exists");
                }

                validateRegisterRole(request.getRole());

                User user = new User();
                user.setFullName(request.getFullName());
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setPhone(request.getPhone());
                user.setRole(request.getRole());
                user.setIsActive(true);

                User savedUser = userRepository.save(user);

                String accessToken = jwtService.generateAccessToken(
                                savedUser.getId(),
                                savedUser.getEmail(),
                                savedUser.getRole().name());

                String refreshToken = jwtService.generateRefreshToken(
                                savedUser.getId(),
                                savedUser.getEmail());

                saveRefreshToken(savedUser, refreshToken);

                return buildAuthResponse(accessToken, refreshToken);
        }

        @Override
        @Transactional
        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String accessToken = jwtService.generateAccessToken(
                                user.getId(),
                                user.getEmail(),
                                user.getRole().name());

                String refreshToken = jwtService.generateRefreshToken(
                                user.getId(),
                                user.getEmail());

                saveRefreshToken(user, refreshToken);

                return buildAuthResponse(accessToken, refreshToken);
        }

        @Override
        @Transactional
        public AuthResponse refreshToken(RefreshTokenRequest request) {
                RefreshToken refreshTokenEntity = refreshTokenRepository
                                .findByTokenAndIsRevokedFalse(request.getRefreshToken())
                                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

                if (refreshTokenEntity.getExpiresAt().isBefore(DateTimeUtils.now())) {
                        throw new UnauthorizedException("Refresh token has expired");
                }

                User user = refreshTokenEntity.getUser();

                if (!jwtService.isTokenValid(request.getRefreshToken(), user.getEmail())) {
                        throw new UnauthorizedException("Invalid refresh token");
                }

                String newAccessToken = jwtService.generateAccessToken(
                                user.getId(),
                                user.getEmail(),
                                user.getRole().name());

                return AuthResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(request.getRefreshToken())
                                .tokenType("Bearer")
                                .accessTokenExpiresIn(jwtService.getAccessTokenExpiration())
                                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpiration())
                                .build();
        }

        private void saveRefreshToken(User user, String token) {
                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setUser(user);
                refreshToken.setToken(token);
                refreshToken.setExpiresAt(
                                DateTimeUtils.now().plusSeconds(jwtService.getRefreshTokenExpiration() / 1000));
                refreshToken.setIsRevoked(false);

                refreshTokenRepository.save(refreshToken);
        }

        private AuthResponse buildAuthResponse(String accessToken, String refreshToken) {
                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .accessTokenExpiresIn(jwtService.getAccessTokenExpiration())
                                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpiration())
                                .build();
        }

        private void validateRegisterRole(UserRole role) {
                if (role == UserRole.ADMIN || role == UserRole.COLLABORATOR) {
                        throw new BadRequestException("You are not allowed to register with this role");
                }
        }
}
