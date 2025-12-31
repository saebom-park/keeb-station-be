package com.saebom.keebstation.global.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(EntityNotFoundException e, HttpServletRequest req) {
        return ResponseEntity.status(404)
                .body(ErrorResponse.of("NOT_FOUND", e.getMessage(), 404, req.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException e, HttpServletRequest req) {
        return ResponseEntity.status(400)
                .body(ErrorResponse.of("BAD_REQUEST", e.getMessage(), 400, req.getRequestURI()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> conflict(IllegalStateException e, HttpServletRequest req) {
        return ResponseEntity.status(409)
                .body(ErrorResponse.of("CONFLICT", e.getMessage(), 409, req.getRequestURI()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> optimisticLock(ObjectOptimisticLockingFailureException e,
                                                        HttpServletRequest req) {
        log.error("[OPTIMISTIC_LOCK] path={}", req.getRequestURI(), e);

        return ResponseEntity.status(409)
                .body(ErrorResponse.of(
                        "OPTIMISTIC_LOCK_CONFLICT",
                        "동시 요청으로 인해 처리가 실패했습니다. 다시 시도해주세요.",
                        409,
                        req.getRequestURI()
                ));
    }

}