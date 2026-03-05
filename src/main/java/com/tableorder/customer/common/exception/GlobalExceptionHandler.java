package com.tableorder.customer.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        log.warn("[ERROR] ApiException | code={}, status={}, message={}", ex.getCode(), ex.getHttpStatus().value(), ex.getMessage());
        if (ex.getDetails() != null) {
            log.warn("[ERROR] ApiException details | {}", ex.getDetails());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("code", ex.getCode());
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        if (ex.getDetails() != null) {
            body.put("details", ex.getDetails());
        }
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("[ERROR] Validation 실패 | {}", message);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("code", "INVALID_REQUEST");
        body.put("message", message);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("[ERROR] 예상치 못한 오류 | {}", ex.getMessage(), ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 500);
        body.put("code", "INTERNAL_ERROR");
        body.put("message", "서버 내부 오류가 발생했습니다");
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
