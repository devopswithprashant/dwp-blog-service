package com.devopswithprashant.service.blog.api.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

//import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UpdateBlogRequest DTO Tests")
class UpdateBlogRequestTests {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("should create request with title and markdown via constructor")
        void shouldCreateWithParameters() {
            UpdateBlogRequest request = new UpdateBlogRequest("Updated Title", "Updated Markdown");
            
            assertThat(request.getTitle()).isEqualTo("Updated Title");
            assertThat(request.getMarkdown()).isEqualTo("Updated Markdown");
        }

        @Test
        @DisplayName("should create empty request with no-arg constructor")
        void shouldCreateEmptyRequest() {
            UpdateBlogRequest request = new UpdateBlogRequest();
            assertThat(request).isNotNull();
        }

        @Test
        @DisplayName("should handle null title in constructor")
        void shouldHandleNullTitle() {
            UpdateBlogRequest request = new UpdateBlogRequest(null, "Content");
            assertThat(request.getTitle()).isNull();
            assertThat(request.getMarkdown()).isEqualTo("Content");
        }

        @Test
        @DisplayName("should handle null markdown in constructor")
        void shouldHandleNullMarkdown() {
            UpdateBlogRequest request = new UpdateBlogRequest("Title", null);
            assertThat(request.getTitle()).isEqualTo("Title");
            assertThat(request.getMarkdown()).isNull();
        }
    }

    @Nested
    @DisplayName("Title Getter/Setter Tests")
    class TitleTests {
        
        @Test
        @DisplayName("should set and get title correctly")
        void shouldSetAndGetTitle() {
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("My Blog Title");
            assertThat(request.getTitle()).isEqualTo("My Blog Title");
        }

        @Test
        @DisplayName("should handle empty title")
        void shouldHandleEmptyTitle() {
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("");
            assertThat(request.getTitle()).isEmpty();
        }

        @Test
        @DisplayName("should handle null title")
        void shouldHandleNullTitle() {
            UpdateBlogRequest request = new UpdateBlogRequest("Original", "Content");
            request.setTitle(null);
            assertThat(request.getTitle()).isNull();
        }

        @Test
        @DisplayName("should replace title with new value")
        void shouldReplaceTitle() {
            UpdateBlogRequest request = new UpdateBlogRequest("Old Title", "Content");
            request.setTitle("New Title");
            assertThat(request.getTitle()).isEqualTo("New Title");
        }

        @Test
        @DisplayName("should preserve title with special characters")
        void shouldPreserveSpecialCharacters() {
            String title = "Blog: Updates & News @ 2024!";
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle(title);
            assertThat(request.getTitle()).isEqualTo(title);
        }

        @Test
        @DisplayName("should handle very long titles")
        void shouldHandleLongTitles() {
            String longTitle = "A".repeat(1000);
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle(longTitle);
            assertThat(request.getTitle()).hasSize(1000);
        }
    }

    @Nested
    @DisplayName("Markdown Getter/Setter Tests")
    class MarkdownTests {
        
        @Test
        @DisplayName("should set and get markdown correctly")
        void shouldSetAndGetMarkdown() {
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setMarkdown("# New Content");
            assertThat(request.getMarkdown()).isEqualTo("# New Content");
        }

        @Test
        @DisplayName("should handle empty markdown")
        void shouldHandleEmptyMarkdown() {
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setMarkdown("");
            assertThat(request.getMarkdown()).isEmpty();
        }

        @Test
        @DisplayName("should handle null markdown")
        void shouldHandleNullMarkdown() {
            UpdateBlogRequest request = new UpdateBlogRequest("Title", "Original");
            request.setMarkdown(null);
            assertThat(request.getMarkdown()).isNull();
        }

        @Test
        @DisplayName("should preserve markdown formatting")
        void shouldPreserveMarkdownFormatting() {
            String markdown = "# Heading\n## Subheading\n**Bold** *Italic*\n\n- Item 1\n- Item 2\n\n```code block```";
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setMarkdown(markdown);
            assertThat(request.getMarkdown()).isEqualTo(markdown);
        }

        @Test
        @DisplayName("should handle multiline content")
        void shouldHandleMultilineContent() {
            String content = "Line 1\nLine 2\nLine 3\nLine 4";
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setMarkdown(content);
            assertThat(request.getMarkdown()).contains("Line 1").contains("Line 4");
        }

        @Test
        @DisplayName("should replace markdown with new value")
        void shouldReplaceMarkdown() {
            UpdateBlogRequest request = new UpdateBlogRequest("Title", "Old Content");
            request.setMarkdown("New Content");
            assertThat(request.getMarkdown()).isEqualTo("New Content");
        }

        @Test
        @DisplayName("should handle markdown with special characters")
        void shouldHandleSpecialCharacters() {
            String markdown = "Special chars: @#$%^&*()_+-=[]{}|;':\",./<>?";
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setMarkdown(markdown);
            assertThat(request.getMarkdown()).isEqualTo(markdown);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("should support full update workflow")
        void shouldSupportFullUpdateWorkflow() {
            // Create
            UpdateBlogRequest request = new UpdateBlogRequest("Original Title", "Original Content");
            
            // Update title
            request.setTitle("Updated Title");
            assertThat(request.getTitle()).isEqualTo("Updated Title");
            assertThat(request.getMarkdown()).isEqualTo("Original Content");
            
            // Update markdown
            request.setMarkdown("Updated Content");
            assertThat(request.getTitle()).isEqualTo("Updated Title");
            assertThat(request.getMarkdown()).isEqualTo("Updated Content");
        }

        @Test
        @DisplayName("should handle independent title and markdown updates")
        void shouldHandleIndependentUpdates() {
            UpdateBlogRequest request = new UpdateBlogRequest();
            
            // Update only title
            request.setTitle("Only Title");
            assertThat(request.getTitle()).isEqualTo("Only Title");
            assertThat(request.getMarkdown()).isNull();
            
            // Update only markdown
            UpdateBlogRequest request2 = new UpdateBlogRequest();
            request2.setMarkdown("Only Markdown");
            assertThat(request2.getTitle()).isNull();
            assertThat(request2.getMarkdown()).isEqualTo("Only Markdown");
        }

        @Test
        @DisplayName("should maintain consistency through multiple updates")
        void shouldMaintainConsistency() {
            UpdateBlogRequest request = new UpdateBlogRequest("Title 1", "Content 1");
            
            // First update
            request.setTitle("Title 2");
            request.setMarkdown("Content 2");
            
            // Verify both updated
            assertThat(request.getTitle()).isEqualTo("Title 2");
            assertThat(request.getMarkdown()).isEqualTo("Content 2");
            
            // Second update - only one field
            request.setTitle("Title 3");
            assertThat(request.getTitle()).isEqualTo("Title 3");
            assertThat(request.getMarkdown()).isEqualTo("Content 2");
        }

        @Test
        @DisplayName("should handle realistic blog update scenario")
        void shouldHandleRealisticUpdateScenario() {
            // Simulating a blog update with complex markdown
            String originalMarkdown = "# Original Blog Post\n\nThis is the original content.";
            String updatedMarkdown = "# Updated Blog Post\n\nThis is the updated content with more details.\n\n## Section\n\nMore content here.";
            
            UpdateBlogRequest request = new UpdateBlogRequest("Blog: Original Title", originalMarkdown);
            
            // Update for publication
            request.setTitle("Blog: Updated Title - Final Version");
            request.setMarkdown(updatedMarkdown);
            
            assertThat(request.getTitle()).isEqualTo("Blog: Updated Title - Final Version");
            assertThat(request.getMarkdown()).contains("## Section");
            assertThat(request.getMarkdown()).doesNotContain("Original content");
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("should return string representation")
        void shouldReturnStringRepresentation() {
            UpdateBlogRequest request = new UpdateBlogRequest("Title", "Content");
            String toString = request.toString();
            assertThat(toString).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("should include class name in toString")
        void shouldIncludeClassName() {
            UpdateBlogRequest request = new UpdateBlogRequest();
            String toString = request.toString();
            assertThat(toString).contains("UpdateBlogRequest");
        }
    }
}
