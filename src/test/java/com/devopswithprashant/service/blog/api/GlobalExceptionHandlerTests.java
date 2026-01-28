package com.devopswithprashant.service.blog.api;

import com.devopswithprashant.service.blog.exception.BlogNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Nested
    @DisplayName("BlogNotFoundException Handler Tests")
    class BlogNotFoundExceptionHandlerTests {
        
        @Test
        @DisplayName("should return 404 NOT_FOUND when BlogNotFoundException is thrown")
        void shouldReturn404WhenBlogNotFound() {
            BlogNotFoundException exception = new BlogNotFoundException(1L);
            
            ResponseEntity<Map<String, Object>> response = handler.handleBlogNotFound(exception);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("should return error message in response body")
        void shouldReturnErrorMessageInBody() {
            BlogNotFoundException exception = new BlogNotFoundException(5L);
            
            ResponseEntity<Map<String, Object>> response = handler.handleBlogNotFound(exception);
            
            assertThat(response.getBody()).isNotNull().isNotEmpty();
            assertThat(response.getBody()).containsKey("message").containsKey("status").containsKey("error");
        }

        @Test
        @DisplayName("should handle BlogNotFoundException with different blog IDs")
        void shouldHandleDifferentBlogIds() {
            BlogNotFoundException exception1 = new BlogNotFoundException(1L);
            BlogNotFoundException exception2 = new BlogNotFoundException(999L);
            
            ResponseEntity<Map<String, Object>> response1 = handler.handleBlogNotFound(exception1);
            ResponseEntity<Map<String, Object>> response2 = handler.handleBlogNotFound(exception2);
            
            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Generic Exception Handler Tests")
    class GenericExceptionHandlerTests {
        
        @Test
        @DisplayName("should return 500 INTERNAL_SERVER_ERROR for generic exceptions")
        void shouldReturn500ForGenericException() {
            Exception exception = new Exception("Some error occurred");
            
            ResponseEntity<Map<String, Object>> response = handler.handleGeneric(exception);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("should return error message in response body")
        void shouldReturnErrorMessageForGenericException() {
            Exception exception = new Exception("Database connection failed");
            
            ResponseEntity<Map<String, Object>> response = handler.handleGeneric(exception);
            
            assertThat(response.getBody()).isNotNull().isNotEmpty();
            assertThat(response.getBody()).containsKey("message").containsKey("status").containsKey("error").containsKey("timestamp");
        }

        @Test
        @DisplayName("should handle different exception types")
        void shouldHandleDifferentExceptionTypes() {
            RuntimeException runtimeException = new RuntimeException("Runtime error");
            IllegalArgumentException argException = new IllegalArgumentException("Invalid argument");
            
            ResponseEntity<Map<String, Object>> response1 = handler.handleGeneric(runtimeException);
            ResponseEntity<Map<String, Object>> response2 = handler.handleGeneric(argException);
            
            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("should handle null exception message gracefully")
        void shouldHandleNullExceptionMessage() {
            Exception exception = new Exception();
            
            ResponseEntity<Map<String, Object>> response = handler.handleGeneric(exception);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Handler Constructor and Instantiation Tests")
    class HandlerInstantiationTests {
        
        @Test
        @DisplayName("should instantiate GlobalExceptionHandler successfully")
        void shouldInstantiateSuccessfully() {
            GlobalExceptionHandler newHandler = new GlobalExceptionHandler();
            assertThat(newHandler).isNotNull();
        }

        @Test
        @DisplayName("should create handler instance without errors")
        void shouldCreateInstanceWithoutErrors() {
            assertThat(new GlobalExceptionHandler()).isInstanceOf(GlobalExceptionHandler.class);
        }
    }
}

