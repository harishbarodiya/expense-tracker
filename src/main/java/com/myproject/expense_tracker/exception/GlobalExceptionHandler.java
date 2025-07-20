package com.myproject.expense_tracker.exception;

import com.myproject.expense_tracker.controller.AdminController;
import com.myproject.expense_tracker.dto.ApiResponseDto;
import com.myproject.expense_tracker.enums.ApiStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST,
            EntityNotFoundException.class, HttpStatus.NOT_FOUND,
            AccessDeniedException.class, HttpStatus.FORBIDDEN,
            BadCredentialsException.class, HttpStatus.UNAUTHORIZED
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleException(Exception exception) {
        HttpStatus httpStatus = EXCEPTION_STATUS_MAP.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);

        logger.error("Exception occurred: {}", exception.getClass());

        return ResponseEntity.status(httpStatus).body(
                new ApiResponseDto<>(
                        ApiStatus.FAILED,
                        httpStatus.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.equals(httpStatus) ? HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() : exception.getMessage(),
                        LocalDateTime.now(),
                        Collections.emptyMap()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}

