package com.gargoil.tmsapi.controller.exception;

import com.gargoil.tmsapi.controller.response.ResponseWrapper;
import com.gargoil.tmsapi.exception.AuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseWrapper> handleValidationException(ValidationException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseWrapper> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseWrapper> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseWrapper> handleExpiredJwtException(ExpiredJwtException ex) {
        log.warn("JWT token expired: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Token has expired"));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ResponseWrapper> handleMalformedJwtException(MalformedJwtException ex) {
        log.warn("Malformed JWT token: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Invalid token format"));
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ResponseWrapper> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        log.warn("Unsupported JWT token: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Unsupported token"));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ResponseWrapper> handleJwtException(JwtException ex) {
        log.warn("JWT error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(HttpStatus.UNAUTHORIZED, "Token validation failed"));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseWrapper.error(HttpStatus.FORBIDDEN, "Access denied"));
    }
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ResponseWrapper> handleEntityExistsException(EntityExistsException ex) {
        log.warn("Entity already exists: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseWrapper.error(HttpStatus.CONFLICT, ex.getMessage()));
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseWrapper> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseWrapper.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Method Argument Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, errors.toString()));
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseWrapper> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("Endpoint not found: {}", ex.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseWrapper.error(HttpStatus.NOT_FOUND, "Endpoint not found"));
    }
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ResponseWrapper> handleRuntimeException(RuntimeException ex) {
//        log.error("Runtime exception: {}", ex.getMessage(), ex);
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
//    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ResponseWrapper> handleGenericException(Exception ex) {
//        log.error("Unexpected exception: {}", ex.getMessage(), ex);
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
//    }
}