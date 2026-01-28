package com.devopswithprashant.service.blog.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BlogPostVersion Domain Entity Tests")
class BlogPostVersionTests {

    @Nested
    @DisplayName("All-Args Constructor Tests")
    class AllArgsConstructorTests {
        
        @Test
        @DisplayName("should create BlogPostVersion with all parameters")
        void shouldCreateWithAllParameters() {
            Instant now = Instant.now();
            BlogPostVersion version = new BlogPostVersion(1L, 100L, 1, "Content v1", now);
            
            assertThat(version.getId()).isEqualTo(1L);
            assertThat(version.getPostId()).isEqualTo(100L);
            assertThat(version.getVersion()).isEqualTo(1);
            assertThat(version.getContent()).isEqualTo("Content v1");
            assertThat(version.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("should handle multiple versions correctly")
        void shouldHandleMultipleVersions() {
            Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
            Instant t2 = Instant.parse("2024-01-02T10:00:00Z");
            Instant t3 = Instant.parse("2024-01-03T10:00:00Z");
            
            BlogPostVersion v1 = new BlogPostVersion(1L, 100L, 1, "Content v1", t1);
            BlogPostVersion v2 = new BlogPostVersion(2L, 100L, 2, "Content v2", t2);
            BlogPostVersion v3 = new BlogPostVersion(3L, 100L, 3, "Content v3", t3);
            
            assertThat(v1.getVersion()).isLessThan(v2.getVersion());
            assertThat(v2.getVersion()).isLessThan(v3.getVersion());
            assertThat(v1.getPostId()).isEqualTo(v2.getPostId()).isEqualTo(v3.getPostId());
        }

        @Test
        @DisplayName("should handle long content strings")
        void shouldHandleLongContent() {
            String longContent = "A".repeat(10000);
            Instant now = Instant.now();
            BlogPostVersion version = new BlogPostVersion(1L, 100L, 1, longContent, now);
            
            assertThat(version.getContent()).hasSize(10000);
        }

        @Test
        @DisplayName("should preserve all field values exactly")
        void shouldPreserveAllFieldValues() {
            Instant timestamp = Instant.parse("2024-01-15T14:30:00Z");
            BlogPostVersion version = new BlogPostVersion(999L, 888L, 42, "Complex content with special chars: @#$%", timestamp);
            
            assertThat(version.getId()).isEqualTo(999L);
            assertThat(version.getPostId()).isEqualTo(888L);
            assertThat(version.getVersion()).isEqualTo(42);
            assertThat(version.getContent()).isEqualTo("Complex content with special chars: @#$%");
            assertThat(version.getCreatedAt()).isEqualTo(timestamp);
        }
    }

    @Nested
    @DisplayName("No-Args Constructor Tests")
    class NoArgsConstructorTests {
        
        @Test
        @DisplayName("should create empty BlogPostVersion with no-arg constructor")
        void shouldCreateEmpty() {
            BlogPostVersion version = new BlogPostVersion();
            assertThat(version).isNotNull();
        }

        @Test
        @DisplayName("should allow setting fields after no-arg construction")
        void shouldAllowSettingFields() {
            BlogPostVersion version = new BlogPostVersion();
            
            version.setId(1L);
            version.setPostId(100L);
            version.setVersion(1);
            version.setContent("Test content");
            version.setCreatedAt(Instant.now());
            
            assertThat(version.getId()).isEqualTo(1L);
            assertThat(version.getPostId()).isEqualTo(100L);
            assertThat(version.getVersion()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {
        
        @Test
        @DisplayName("should set and get id correctly")
        void shouldSetAndGetId() {
            BlogPostVersion version = new BlogPostVersion();
            version.setId(5L);
            assertThat(version.getId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("should set and get postId correctly")
        void shouldSetAndGetPostId() {
            BlogPostVersion version = new BlogPostVersion();
            version.setPostId(200L);
            assertThat(version.getPostId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("should set and get version number correctly")
        void shouldSetAndGetVersion() {
            BlogPostVersion version = new BlogPostVersion();
            version.setVersion(5);
            assertThat(version.getVersion()).isEqualTo(5);
        }

        @Test
        @DisplayName("should set and get content correctly")
        void shouldSetAndGetContent() {
            BlogPostVersion version = new BlogPostVersion();
            String content = "# Heading\n\nContent paragraph";
            version.setContent(content);
            assertThat(version.getContent()).isEqualTo(content);
        }

        @Test
        @DisplayName("should set and get createdAt correctly")
        void shouldSetAndGetCreatedAt() {
            BlogPostVersion version = new BlogPostVersion();
            Instant timestamp = Instant.now();
            version.setCreatedAt(timestamp);
            assertThat(version.getCreatedAt()).isEqualTo(timestamp);
        }

        @Test
        @DisplayName("should handle null id")
        void shouldHandleNullId() {
            BlogPostVersion version = new BlogPostVersion();
            version.setId(null);
            assertThat(version.getId()).isNull();
        }

        @Test
        @DisplayName("should handle null content")
        void shouldHandleNullContent() {
            BlogPostVersion version = new BlogPostVersion();
            version.setContent(null);
            assertThat(version.getContent()).isNull();
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("should return string representation")
        void shouldReturnStringRepresentation() {
            BlogPostVersion version = new BlogPostVersion(1L, 100L, 1, "Content", Instant.now());
            String str = version.toString();
            assertThat(str).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("should include class name in toString")
        void shouldIncludeClassName() {
            BlogPostVersion version = new BlogPostVersion();
            assertThat(version.toString()).contains("BlogPostVersion");
        }
    }

    @Nested
    @DisplayName("Version Management Tests")
    class VersionManagementTests {
        
        @Test
        @DisplayName("should increment version numbers correctly")
        void shouldIncrementVersionNumbers() {
            BlogPostVersion v1 = new BlogPostVersion();
            v1.setVersion(1);
            
            BlogPostVersion v2 = new BlogPostVersion();
            v2.setVersion(v1.getVersion() + 1);
            
            BlogPostVersion v3 = new BlogPostVersion();
            v3.setVersion(v2.getVersion() + 1);
            
            assertThat(v1.getVersion()).isEqualTo(1);
            assertThat(v2.getVersion()).isEqualTo(2);
            assertThat(v3.getVersion()).isEqualTo(3);
        }

        @Test
        @DisplayName("should track version history for same post")
        void shouldTrackVersionHistory() {
            Instant t1 = Instant.now();
            Instant t2 = t1.plusSeconds(3600);
            Instant t3 = t1.plusSeconds(7200);
            
            BlogPostVersion v1 = new BlogPostVersion(1L, 100L, 1, "First version", t1);
            BlogPostVersion v2 = new BlogPostVersion(2L, 100L, 2, "Second version", t2);
            BlogPostVersion v3 = new BlogPostVersion(3L, 100L, 3, "Third version", t3);
            
            // Verify all versions are for the same post
            assertThat(v1.getPostId()).isEqualTo(v2.getPostId()).isEqualTo(v3.getPostId());
            
            // Verify versions are sequential
            assertThat(v1.getVersion()).isLessThan(v2.getVersion()).isLessThan(v3.getVersion());
            
            // Verify timestamps are sequential
            assertThat(v1.getCreatedAt()).isBefore(v2.getCreatedAt());
            assertThat(v2.getCreatedAt()).isBefore(v3.getCreatedAt());
        }

        @Test
        @DisplayName("should handle large version numbers")
        void shouldHandleLargeVersionNumbers() {
            BlogPostVersion version = new BlogPostVersion();
            version.setVersion(9999);
            assertThat(version.getVersion()).isEqualTo(9999);
        }
    }

    @Nested
    @DisplayName("Content Tracking Tests")
    class ContentTrackingTests {
        
        @Test
        @DisplayName("should preserve markdown content across versions")
        void shouldPreserveMarkdownContent() {
            String markdown = "# Blog Title\n## Section 1\n\nContent here\n\n## Section 2\n\nMore content";
            
            BlogPostVersion v1 = new BlogPostVersion();
            v1.setContent(markdown);
            
            assertThat(v1.getContent()).isEqualTo(markdown);
            assertThat(v1.getContent()).contains("## Section 1").contains("## Section 2");
        }

        @Test
        @DisplayName("should handle empty content")
        void shouldHandleEmptyContent() {
            BlogPostVersion version = new BlogPostVersion();
            version.setContent("");
            assertThat(version.getContent()).isEmpty();
        }

        @Test
        @DisplayName("should handle multiline content with special characters")
        void shouldHandleSpecialCharacters() {
            String content = "Content with special chars: @#$%^&*()_+-=[]{}|;':\",./<>?\n\nNew line with unicode: é ñ ü";
            BlogPostVersion version = new BlogPostVersion();
            version.setContent(content);
            assertThat(version.getContent()).isEqualTo(content);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("should handle complete version history lifecycle")
        void shouldHandleCompleteVersionHistory() {
            Instant created = Instant.now();
            
            // Create initial version
            BlogPostVersion v1 = new BlogPostVersion();
            v1.setId(1L);
            v1.setPostId(100L);
            v1.setVersion(1);
            v1.setContent("Initial content");
            v1.setCreatedAt(created);
            
            // Create update version
            BlogPostVersion v2 = new BlogPostVersion();
            v2.setId(2L);
            v2.setPostId(100L);
            v2.setVersion(2);
            v2.setContent("Updated content with more details");
            v2.setCreatedAt(created.plusSeconds(3600));
            
            // Create final version
            BlogPostVersion v3 = new BlogPostVersion();
            v3.setId(3L);
            v3.setPostId(100L);
            v3.setVersion(3);
            v3.setContent("Final version ready for publishing");
            v3.setCreatedAt(created.plusSeconds(7200));
            
            // Verify complete history
            assertThat(v1.getPostId()).isEqualTo(v2.getPostId()).isEqualTo(v3.getPostId());
            assertThat(v1.getVersion()).isEqualTo(1);
            assertThat(v2.getVersion()).isEqualTo(2);
            assertThat(v3.getVersion()).isEqualTo(3);
            assertThat(v1.getCreatedAt()).isBefore(v2.getCreatedAt());
            assertThat(v2.getCreatedAt()).isBefore(v3.getCreatedAt());
        }
    }
}
