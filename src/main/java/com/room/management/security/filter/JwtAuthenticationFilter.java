package com.room.management.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.room.management.entity.auth.UserToken;
import com.room.management.security.context.SecurityContext;
import com.room.management.security.service.PermissionValidationService;
import com.room.management.security.service.TokenValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenValidationService tokenValidationService;
    private final PermissionValidationService permissionValidationService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractBearerToken(request);

            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<UserToken> tokenOpt = tokenValidationService.validateAndLoadToken(token);

            if (tokenOpt.isEmpty()) {
                writeUnauthorizedResponse(response, "Invalid or expired token");
                return;
            }

            UserToken userToken = tokenOpt.get();
            String requestUri = request.getRequestURI();
            String httpMethod = request.getMethod();

            if (!permissionValidationService.hasPermission(userToken, requestUri, httpMethod)) {
                writeForbiddenResponse(response, "Insufficient permissions to access this resource");
                return;
            }

            SecurityContext.set(userToken, userToken.getUser());
            setSpringSecurityContext(userToken);

            filterChain.doFilter(request, response);
        } finally {
            SecurityContext.clear();
        }
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void setSpringSecurityContext(UserToken userToken) {
        List<SimpleGrantedAuthority> authorities = userToken.getUser().getActiveRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRoleName()))
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userToken.getUser().getUsername(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), message);
    }

    private void writeForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        writeErrorResponse(response, HttpStatus.FORBIDDEN.value(), message);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of(
                "success", false,
                "status", status,
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
