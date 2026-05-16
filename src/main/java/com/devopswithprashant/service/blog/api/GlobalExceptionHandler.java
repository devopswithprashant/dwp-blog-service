package com.devopswithprashant.service.blog.api;

import com.devopswithprashant.service.blog.exception.BlogNotFoundException;
import com.devopswithprashant.service.blog.infrastructure.metrics.BlogMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final BlogMetrics blogMetrics;

    public GlobalExceptionHandler(BlogMetrics blogMetrics) {
        this.blogMetrics = blogMetrics;
    }

    @ExceptionHandler(BlogNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBlogNotFound(BlogNotFoundException ex) {
        blogMetrics.recordPostNotFound();
        log.warn("Blog not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", Instant.now(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", Instant.now(),
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", "Something went wrong"
                )
        );
    }
}
