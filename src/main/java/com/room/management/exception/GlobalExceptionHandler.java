package com.room.management.exception;

import com.room.management.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Duplicate resource [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> error instanceof FieldError fe ? fe.getField() : error.getObjectName(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, duplicate) -> existing
                ));
        log.warn("Validation failed [{}]: {} error(s)", request.getRequestURI(), errors.size());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request [{}]: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error [{}]", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred. Please try again later."));
    }
}
