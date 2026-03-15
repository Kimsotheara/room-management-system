package com.room.management.security.service;

import com.room.management.entity.auth.UserToken;
import com.room.management.repository.UserTokenRepository;
import com.room.management.service.auth.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationService {

    private final JwtTokenService jwtTokenService;
    private final UserTokenRepository userTokenRepository;

    public Optional<UserToken> validateAndLoadToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }

        if (!jwtTokenService.isTokenValid(rawToken)) {
            log.debug("JWT signature/format validation failed");
            return Optional.empty();
        }

        Optional<UserToken> tokenOpt = userTokenRepository.findValidTokenWithUserAndRoles(rawToken);

        if (tokenOpt.isEmpty()) {
            log.debug("Token not found in database or already revoked");
            return Optional.empty();
        }

        UserToken token = tokenOpt.get();

        if (token.isExpired()) {
            log.debug("Token has expired for user: {}", token.getUser().getUsername());
            return Optional.empty();
        }

        if (!Boolean.TRUE.equals(token.getUser().getIsActive())) {
            log.debug("User account is inactive: {}", token.getUser().getUsername());
            return Optional.empty();
        }

        return Optional.of(token);
    }
}
