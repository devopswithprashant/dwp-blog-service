package com.devopswithprashant.service.blog.api.dto;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BlogMetadataResponse DTO Tests")
class BlogMetadataResponseTests {

    @Nested
    @DisplayName("All-Args Constructor Tests")
    class AllArgsConstructorTests {
        
        @Test
        @DisplayName("should create response with all parameters")
        void shouldCreateWithAllParameters() {
            Instant created = Instant.now();
            Instant published = created.plusSeconds(3600);
            
            BlogMetadataResponse response = new BlogMetadataResponse(
                1L, 101L, "Test Blog", "test-blog", 
                "PUBLISHED", created, published
            );
            
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getAuthorId()).isEqualTo(101L);
            assertThat(response.getTitle()).isEqualTo("Test Blog");
            assertThat(response.getSlug()).isEqualTo("test-blog");
            assertThat(response.getStatus()).isEqualTo("PUBLISHED");
            assertThat(response.getCreatedAt()).isEqualTo(created);
            assertThat(response.getPublishedAt()).isEqualTo(published);
        }

        @Test
        @DisplayName("should handle draft blog with null publishedAt")
        void shouldHandleDraftBlog() {
            Instant created = Instant.now();
            
            BlogMetadataResponse response = new BlogMetadataResponse(
                2L, 102L, "Draft Blog", "draft-blog", 
                "DRAFT", created, null
            );
            
            assertThat(response.getId()).isEqualTo(2L);
            assertThat(response.getStatus()).isEqualTo("DRAFT");
            assertThat(response.getPublishedAt()).isNull();
            assertThat(response.getCreatedAt()).isEqualTo(created);
        }

        @Test
        @DisplayName("should preserve all field values exactly")
        void shouldPreserveAllFieldValues() {
            Instant t1 = Instant.parse("2024-01-01T00:00:00Z");
            Instant t2 = Instant.parse("2024-01-15T12:00:00Z");
            
            BlogMetadataResponse response = new BlogMetadataResponse(
                999L, 888L, "Complex: Title 2024", "complex-title-2024", 
                "PUBLISHED", t1, t2
            );
            
            assertThat(response.getId()).isEqualTo(999L);
            assertThat(response.getAuthorId()).isEqualTo(888L);
            assertThat(response.getTitle()).isEqualTo("Complex: Title 2024");
            assertThat(response.getSlug()).isEqualTo("complex-title-2024");
            assertThat(response.getStatus()).isEqualTo("PUBLISHED");
            assertThat(response.getCreatedAt()).isEqualTo(t1);
            assertThat(response.getPublishedAt()).isEqualTo(t2);
        }
    }

    @Nested
    @DisplayName("No-Args Constructor Tests")
    class NoArgsConstructorTests {
        
        @Test
        @DisplayName("should create empty response with no-arg constructor")
        void shouldCreateEmpty() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("should allow setting fields after no-arg construction")
        void shouldAllowSettingFields() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            Instant now = Instant.now();
            
            response.setId(1L);
            response.setAuthorId(101L);
            response.setTitle("Title");
            response.setSlug("title");
            response.setStatus("DRAFT");
            response.setCreatedAt(now);
            response.setPublishedAt(null);
            
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getAuthorId()).isEqualTo(101L);
        }
    }

    @Nested
    @DisplayName("Id Getter/Setter Tests")
    class IdTests {
        
        @Test
        @DisplayName("should set and get id correctly")
        void shouldSetAndGetId() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setId(10L);
            assertThat(response.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("should handle null id")
        void shouldHandleNullId() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setId(null);
            assertThat(response.getId()).isNull();
        }
    }

    @Nested
    @DisplayName("AuthorId Getter/Setter Tests")
    class AuthorIdTests {
        
        @Test
        @DisplayName("should set and get authorId correctly")
        void shouldSetAndGetAuthorId() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setAuthorId(500L);
            assertThat(response.getAuthorId()).isEqualTo(500L);
        }

        @Test
        @DisplayName("should handle null authorId")
        void shouldHandleNullAuthorId() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setAuthorId(null);
            assertThat(response.getAuthorId()).isNull();
        }
    }

    @Nested
    @DisplayName("Title Getter/Setter Tests")
    class TitleTests {
        
        @Test
        @DisplayName("should set and get title correctly")
        void shouldSetAndGetTitle() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setTitle("My Blog Title");
            assertThat(response.getTitle()).isEqualTo("My Blog Title");
        }

        @Test
        @DisplayName("should handle empty title")
        void shouldHandleEmptyTitle() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setTitle("");
            assertThat(response.getTitle()).isEmpty();
        }

        @Test
        @DisplayName("should handle null title")
        void shouldHandleNullTitle() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setTitle(null);
            assertThat(response.getTitle()).isNull();
        }

        @Test
        @DisplayName("should preserve special characters in title")
        void shouldPreserveSpecialCharacters() {
            String title = "Blog: Updates & News @ 2024!";
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setTitle(title);
            assertThat(response.getTitle()).isEqualTo(title);
        }
    }

    @Nested
    @DisplayName("Slug Getter/Setter Tests")
    class SlugTests {
        
        @Test
        @DisplayName("should set and get slug correctly")
        void shouldSetAndGetSlug() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setSlug("blog-title-slug");
            assertThat(response.getSlug()).isEqualTo("blog-title-slug");
        }

        @Test
        @DisplayName("should handle null slug")
        void shouldHandleNullSlug() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setSlug(null);
            assertThat(response.getSlug()).isNull();
        }

        @Test
        @DisplayName("should handle slugs with various formats")
        void shouldHandleVariousSlugFormats() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            
            response.setSlug("simple-slug");
            assertThat(response.getSlug()).isEqualTo("simple-slug");
            
            response.setSlug("slug-with-numbers-123");
            assertThat(response.getSlug()).contains("123");
            
            response.setSlug("long-slug-with-many-words-describing-content");
            assertThat(response.getSlug()).hasSize(44);
        }
    }

    @Nested
    @DisplayName("Status Getter/Setter Tests")
    class StatusTests {
        
        @Test
        @DisplayName("should set and get status correctly")
        void shouldSetAndGetStatus() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setStatus("PUBLISHED");
            assertThat(response.getStatus()).isEqualTo("PUBLISHED");
        }

        @Test
        @DisplayName("should handle draft status")
        void shouldHandleDraftStatus() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setStatus("DRAFT");
            assertThat(response.getStatus()).isEqualTo("DRAFT");
        }

        @Test
        @DisplayName("should handle null status")
        void shouldHandleNullStatus() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setStatus(null);
            assertThat(response.getStatus()).isNull();
        }

        @Test
        @DisplayName("should handle different status values")
        void shouldHandleDifferentStatusValues() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            
            response.setStatus("DRAFT");
            assertThat(response.getStatus()).isEqualTo("DRAFT");
            
            response.setStatus("PUBLISHED");
            assertThat(response.getStatus()).isEqualTo("PUBLISHED");
            
            response.setStatus("ARCHIVED");
            assertThat(response.getStatus()).isEqualTo("ARCHIVED");
        }
    }

    @Nested
    @DisplayName("CreatedAt Getter/Setter Tests")
    class CreatedAtTests {
        
        @Test
        @DisplayName("should set and get createdAt correctly")
        void shouldSetAndGetCreatedAt() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            Instant created = Instant.now();
            response.setCreatedAt(created);
            assertThat(response.getCreatedAt()).isEqualTo(created);
        }

        @Test
        @DisplayName("should handle null createdAt")
        void shouldHandleNullCreatedAt() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setCreatedAt(null);
            assertThat(response.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("should handle specific timestamps")
        void shouldHandleSpecificTimestamps() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            Instant timestamp = Instant.parse("2024-01-15T14:30:00Z");
            response.setCreatedAt(timestamp);
            assertThat(response.getCreatedAt()).isEqualTo(timestamp);
        }
    }

    @Nested
    @DisplayName("PublishedAt Getter/Setter Tests")
    class PublishedAtTests {
        
        @Test
        @DisplayName("should set and get publishedAt correctly")
        void shouldSetAndGetPublishedAt() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            Instant published = Instant.now();
            response.setPublishedAt(published);
            assertThat(response.getPublishedAt()).isEqualTo(published);
        }

        @Test
        @DisplayName("should handle null publishedAt for drafts")
        void shouldHandleNullPublishedAt() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setPublishedAt(null);
            assertThat(response.getPublishedAt()).isNull();
        }

        @Test
        @DisplayName("should handle specific publish timestamps")
        void shouldHandleSpecificPublishTimestamps() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            Instant timestamp = Instant.parse("2024-01-20T10:00:00Z");
            response.setPublishedAt(timestamp);
            assertThat(response.getPublishedAt()).isEqualTo(timestamp);
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("should return string representation")
        void shouldReturnStringRepresentation() {
            Instant now = Instant.now();
            BlogMetadataResponse response = new BlogMetadataResponse(
                1L, 101L, "Title", "title", "PUBLISHED", now, now
            );
            String str = response.toString();
            assertThat(str).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("should include class name in toString")
        void shouldIncludeClassName() {
            BlogMetadataResponse response = new BlogMetadataResponse();
            assertThat(response.toString()).contains("BlogMetadataResponse");
        }
    }

    @Nested
    @DisplayName("Integration/Scenario Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("should handle draft to published transition")
        void shouldHandleDraftToPublishedTransition() {
            Instant created = Instant.parse("2024-01-01T10:00:00Z");
            Instant published = Instant.parse("2024-01-15T14:00:00Z");
            
            // Create as draft
            BlogMetadataResponse response = new BlogMetadataResponse(
                1L, 101L, "My Blog", "my-blog", 
                "DRAFT", created, null
            );
            
            // Transition to published
            response.setStatus("PUBLISHED");
            response.setPublishedAt(published);
            
            // Verify final state
            assertThat(response.getStatus()).isEqualTo("PUBLISHED");
            assertThat(response.getPublishedAt()).isEqualTo(published);
            assertThat(response.getCreatedAt()).isEqualTo(created);
        }

        @Test
        @DisplayName("should handle complete response lifecycle")
        void shouldHandleCompleteLifecycle() {
            // Build response from parts
            BlogMetadataResponse response = new BlogMetadataResponse();
            response.setId(1L);
            response.setAuthorId(101L);
            response.setTitle("Spring Boot Best Practices");
            response.setSlug("spring-boot-best-practices");
            response.setStatus("PUBLISHED");
            
            Instant created = Instant.parse("2024-01-10T08:00:00Z");
            Instant published = Instant.parse("2024-01-15T12:00:00Z");
            response.setCreatedAt(created);
            response.setPublishedAt(published);
            
            // Verify all fields
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getAuthorId()).isEqualTo(101L);
            assertThat(response.getTitle()).contains("Spring Boot");
            assertThat(response.getSlug()).contains("best-practices");
            assertThat(response.getStatus()).isEqualTo("PUBLISHED");
            assertThat(response.getCreatedAt()).isBefore(response.getPublishedAt());
        }

        @Test
        @DisplayName("should handle multiple response objects independently")
        void shouldHandleMultipleResponses() {
            Instant created1 = Instant.parse("2024-01-01T10:00:00Z");
            Instant created2 = Instant.parse("2024-01-05T10:00:00Z");
            
            BlogMetadataResponse response1 = new BlogMetadataResponse(
                1L, 101L, "Blog 1", "blog-1", "DRAFT", created1, null
            );
            
            BlogMetadataResponse response2 = new BlogMetadataResponse(
                2L, 102L, "Blog 2", "blog-2", "PUBLISHED", created2, created2.plusSeconds(3600)
            );
            
            // Verify independence
            assertThat(response1.getId()).isNotEqualTo(response2.getId());
            assertThat(response1.getStatus()).isNotEqualTo(response2.getStatus());
            assertThat(response1.getPublishedAt()).isNull();
            assertThat(response2.getPublishedAt()).isNotNull();
        }
    }
}
