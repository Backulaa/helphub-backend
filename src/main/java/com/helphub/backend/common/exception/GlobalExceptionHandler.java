package com.helphub.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.helphub.backend.common.payload.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // 404 - Not Found
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.builder()
                                                .success(false)
                                                .message(ex.getMessage())
                                                .data(null)
                                                .build());
        }

        // 400 - Validation
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
                String message = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Validation error");

                return ResponseEntity.badRequest()
                                .body(ApiResponse.builder()
                                                .success(false)
                                                .message(message)
                                                .data(null)
                                                .build());
        }

        // 409 - Email Exists
        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<Object>> handleEmailExists(EmailAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.builder()
                                                .success(false)
                                                .message(ex.getMessage())
                                                .data(null)
                                                .build());
        }

        // 401 - Login Failed
        @ExceptionHandler({ UnauthorizedException.class, BadCredentialsException.class })
        public ResponseEntity<ApiResponse<Object>> handleUnauthorized(Exception ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.builder()
                                                .success(false)
                                                .message(ex.getMessage())
                                                .data(null)
                                                .build());
        }

        // 403 - Forbidden
        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiResponse<Object>> handleForbidden(ForbiddenException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.builder()
                                                .success(false)
                                                .message(ex.getMessage())
                                                .data(null)
                                                .build());
        }

        // 400 - Generic bad request
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
                return ResponseEntity.badRequest()
                                .body(ApiResponse.builder()
                                                .success(false)
                                                .message(ex.getMessage())
                                                .data(null)
                                                .build());
        }

        // 500 - fallback
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.builder()
                                                .success(false)
                                                .message("Internal server error")
                                                .data(null)
                                                .build());
        }
}
