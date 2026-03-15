package com.room.management.service.auth;

import com.room.management.dto.request.LoginRequestDto;
import com.room.management.dto.request.RefreshTokenRequestDto;
import com.room.management.dto.response.LoginResponseDto;
import com.room.management.entity.auth.User;
import com.room.management.entity.auth.UserToken;
import com.room.management.exception.AuthenticationException;
import com.room.management.repository.UserRepository;
import com.room.management.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findActiveUserWithRolesByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        String accessToken = jwtTokenService.generateAccessToken(user.getUserId(), user.getUsername(), user.getEmail());
        String refreshToken = jwtTokenService.generateRefreshToken(user.getUserId(), user.getUsername(), user.getEmail());

        List<String> permissionCodes = user.getAllPermissionCodes().stream().toList();

        UserToken accessTokenEntity = buildUserToken(user, accessToken,
                JwtTokenService.TOKEN_TYPE_ACCESS,
                jwtTokenService.getAccessTokenExpirationSeconds(),
                request.getDeviceId(), request.getDeviceName(), request.getDeviceType(),
                permissionCodes);

        UserToken refreshTokenEntity = buildUserToken(user, refreshToken,
                JwtTokenService.TOKEN_TYPE_REFRESH,
                jwtTokenService.getRefreshTokenExpirationSeconds(),
                request.getDeviceId(), request.getDeviceName(), request.getDeviceType(),
                permissionCodes);

        userTokenRepository.save(accessTokenEntity);
        userTokenRepository.save(refreshTokenEntity);

        log.info("User logged in successfully: {}", user.getUsername());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenService.getAccessTokenExpirationSeconds())
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .permissionCodes(permissionCodes)
                .build();
    }

    @Transactional
    public LoginResponseDto refreshToken(RefreshTokenRequestDto request) {
        UserToken refreshToken = userTokenRepository
                .findValidTokenWithUserAndRoles(request.getRefreshToken())
                .orElseThrow(() -> new AuthenticationException("Invalid or expired refresh token"));

        if (!JwtTokenService.TOKEN_TYPE_REFRESH.equals(refreshToken.getTokenType())) {
            throw new AuthenticationException("Provided token is not a refresh token");
        }

        if (refreshToken.isExpired()) {
            throw new AuthenticationException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        String newAccessToken = jwtTokenService.generateAccessToken(user.getUserId(), user.getUsername(), user.getEmail());
        String newRefreshToken = jwtTokenService.generateRefreshToken(user.getUserId(), user.getUsername(), user.getEmail());

        List<String> permissionCodes = user.getAllPermissionCodes().stream().toList();

        refreshToken.setRevoked(true);
        userTokenRepository.save(refreshToken);

        UserToken newAccessTokenEntity = buildUserToken(user, newAccessToken,
                JwtTokenService.TOKEN_TYPE_ACCESS,
                jwtTokenService.getAccessTokenExpirationSeconds(),
                refreshToken.getDeviceId(), refreshToken.getDeviceName(), refreshToken.getDeviceType(),
                permissionCodes);

        UserToken newRefreshTokenEntity = buildUserToken(user, newRefreshToken,
                JwtTokenService.TOKEN_TYPE_REFRESH,
                jwtTokenService.getRefreshTokenExpirationSeconds(),
                refreshToken.getDeviceId(), refreshToken.getDeviceName(), refreshToken.getDeviceType(),
                permissionCodes);

        userTokenRepository.save(newAccessTokenEntity);
        userTokenRepository.save(newRefreshTokenEntity);

        log.info("Token refreshed successfully for user: {}", user.getUsername());

        return LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenService.getAccessTokenExpirationSeconds())
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .permissionCodes(permissionCodes)
                .build();
    }

    @Transactional
    public void logout(String rawToken) {
        userTokenRepository.findValidTokenWithUserAndRoles(rawToken).ifPresent(token -> {
            Long userId = token.getUser().getUserId();
            userTokenRepository.revokeAllTokensByUserId(userId);
            log.info("User logged out: {}", token.getUser().getUsername());
        });
    }

    private UserToken buildUserToken(User user, String tokenValue, String tokenType,
                                      long expirationSeconds, String deviceId,
                                      String deviceName, String deviceType,
                                      List<String> permissionCodes) {
        UserToken token = new UserToken();
        token.setUser(user);
        token.setTokenValue(tokenValue);
        token.setTokenType(tokenType);
        token.setDeviceId(deviceId);
        token.setDeviceName(deviceName);
        token.setDeviceType(deviceType);
        token.setExpiryDate(LocalDateTime.now().plusSeconds(expirationSeconds));
        token.setRevoked(false);
        token.setPermissionCodesList(permissionCodes);
        return token;
    }
}
