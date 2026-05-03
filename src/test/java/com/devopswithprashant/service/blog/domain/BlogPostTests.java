package com.devopswithprashant.service.blog.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BlogPost Domain Entity Tests")
class BlogPostTests {

    @Nested
    @DisplayName("All-Args Constructor Tests")
    class AllArgsConstructorTests {
        
        @Test
        @DisplayName("should create BlogPost with all parameters")
        void shouldCreateWithAllParameters() {
            Instant now = Instant.now();
            BlogPost post = new BlogPost(1L, 101L, "Test Blog", "test-blog", 
                PostStatus.DRAFT, now, now, null);
            
            assertThat(post.getId()).isEqualTo(1L);
            assertThat(post.getAuthorId()).isEqualTo(101L);
            assertThat(post.getTitle()).isEqualTo("Test Blog");
            assertThat(post.getSlug()).isEqualTo("test-blog");
            assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
            assertThat(post.getCreatedAt()).isEqualTo(now);
            assertThat(post.getUpdatedAt()).isEqualTo(now);
            assertThat(post.getPublishedAt()).isNull();
        }

        @Test
        @DisplayName("should handle published blog with publishedAt timestamp")
        void shouldHandlePublishedBlog() {
            Instant created = Instant.parse("2024-01-01T10:00:00Z");
            Instant published = Instant.parse("2024-01-05T15:30:00Z");
            
            BlogPost post = new BlogPost(2L, 102L, "Published Blog", "published-blog", 
                PostStatus.PUBLISHED, created, created, published);
            
            assertThat(post.getPublishedAt()).isEqualTo(published);
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        }

        @Test
        @DisplayName("should handle null id in constructor")
        void shouldHandleNullId() {
            Instant now = Instant.now();
            BlogPost post = new BlogPost(null, 101L, "Title", "title", 
                PostStatus.DRAFT, now, now, null);
            
            assertThat(post.getId()).isNull();
        }

        @Test
        @DisplayName("should preserve all field values exactly")
        void shouldPreserveAllFieldValues() {
            Instant t1 = Instant.parse("2024-01-01T00:00:00Z");
            Instant t2 = Instant.parse("2024-01-10T00:00:00Z");
            Instant t3 = Instant.parse("2024-01-15T00:00:00Z");
            
            BlogPost post = new BlogPost(999L, 888L, "Complex Title: 2024", "complex-title-2024", 
                PostStatus.PUBLISHED, t1, t2, t3);
            
            assertThat(post.getId()).isEqualTo(999L);
            assertThat(post.getAuthorId()).isEqualTo(888L);
            assertThat(post.getTitle()).isEqualTo("Complex Title: 2024");
            assertThat(post.getSlug()).isEqualTo("complex-title-2024");
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
            assertThat(post.getCreatedAt()).isEqualTo(t1);
            assertThat(post.getUpdatedAt()).isEqualTo(t2);
            assertThat(post.getPublishedAt()).isEqualTo(t3);
        }
    }

    @Nested
    @DisplayName("No-Args Constructor Tests")
    class NoArgsConstructorTests {
        
        @Test
        @DisplayName("should create empty BlogPost with no-arg constructor")
        void shouldCreateEmptyBlogPost() {
            BlogPost post = new BlogPost();
            assertThat(post).isNotNull();
        }

        @Test
        @DisplayName("should allow setting fields after no-arg construction")
        void shouldAllowSettingFields() {
            BlogPost post = new BlogPost();
            
            post.setId(1L);
            post.setAuthorId(101L);
            post.setTitle("Test");
            post.setSlug("test");
            post.setStatus(PostStatus.DRAFT);
            
            assertThat(post.getId()).isEqualTo(1L);
            assertThat(post.getAuthorId()).isEqualTo(101L);
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {
        
        @Test
        @DisplayName("should set and get id correctly")
        void shouldSetAndGetId() {
            BlogPost post = new BlogPost();
            post.setId(10L);
            assertThat(post.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("should set and get authorId correctly")
        void shouldSetAndGetAuthorId() {
            BlogPost post = new BlogPost();
            post.setAuthorId(500L);
            assertThat(post.getAuthorId()).isEqualTo(500L);
        }

        @Test
        @DisplayName("should set and get title correctly")
        void shouldSetAndGetTitle() {
            BlogPost post = new BlogPost();
            post.setTitle("New Title");
            assertThat(post.getTitle()).isEqualTo("New Title");
        }

        @Test
        @DisplayName("should set and get slug correctly")
        void shouldSetAndGetSlug() {
            BlogPost post = new BlogPost();
            post.setSlug("new-slug");
            assertThat(post.getSlug()).isEqualTo("new-slug");
        }

        @Test
        @DisplayName("should set and get status correctly")
        void shouldSetAndGetStatus() {
            BlogPost post = new BlogPost();
            post.setStatus(PostStatus.PUBLISHED);
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        }

        @Test
        @DisplayName("should set and get createdAt correctly")
        void shouldSetAndGetCreatedAt() {
            BlogPost post = new BlogPost();
            Instant now = Instant.now();
            post.setCreatedAt(now);
            assertThat(post.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("should set and get updatedAt correctly")
        void shouldSetAndGetUpdatedAt() {
            BlogPost post = new BlogPost();
            Instant updated = Instant.now();
            post.setUpdatedAt(updated);
            assertThat(post.getUpdatedAt()).isEqualTo(updated);
        }

        @Test
        @DisplayName("should set and get publishedAt correctly")
        void shouldSetAndGetPublishedAt() {
            BlogPost post = new BlogPost();
            Instant published = Instant.now();
            post.setPublishedAt(published);
            assertThat(post.getPublishedAt()).isEqualTo(published);
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("should return string representation")
        void shouldReturnStringRepresentation() {
            BlogPost post = new BlogPost(1L, 101L, "Blog", "blog", PostStatus.DRAFT, 
                Instant.now(), Instant.now(), null);
            String str = post.toString();
            assertThat(str).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("should include class name in toString")
        void shouldIncludeClassName() {
            BlogPost post = new BlogPost();
            assertThat(post.toString()).contains("BlogPost");
        }
    }

    @Nested
    @DisplayName("Status Transition Tests")
    class StatusTransitionTests {
        
        @Test
        @DisplayName("should transition from DRAFT to PUBLISHED")
        void shouldTransitionFromDraftToPublished() {
            BlogPost post = new BlogPost();
            post.setStatus(PostStatus.DRAFT);
            assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
            
            post.setStatus(PostStatus.PUBLISHED);
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        }

        @Test
        @DisplayName("should transition from PUBLISHED back to DRAFT")
        void shouldTransitionFromPublishedToDraft() {
            BlogPost post = new BlogPost();
            post.setStatus(PostStatus.PUBLISHED);
            post.setStatus(PostStatus.DRAFT);
            assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("should handle complete blog lifecycle")
        void shouldHandleCompleteBlogLifecycle() {
            // Create draft
            Instant created = Instant.now();
            BlogPost post = new BlogPost();
            post.setId(1L);
            post.setAuthorId(100L);
            post.setTitle("My First Blog");
            post.setSlug("my-first-blog");
            post.setStatus(PostStatus.DRAFT);
            post.setCreatedAt(created);
            post.setUpdatedAt(created);
            
            // Update content
            Instant updated = created.plusSeconds(3600);
            post.setUpdatedAt(updated);
            post.setTitle("My First Blog - Updated");
            
            // Publish
            Instant published = created.plusSeconds(7200);
            post.setStatus(PostStatus.PUBLISHED);
            post.setPublishedAt(published);
            
            // Verify complete state
            assertThat(post.getId()).isEqualTo(1L);
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
            assertThat(post.getPublishedAt()).isEqualTo(published);
            assertThat(post.getUpdatedAt()).isEqualTo(updated);
        }
    }
}
