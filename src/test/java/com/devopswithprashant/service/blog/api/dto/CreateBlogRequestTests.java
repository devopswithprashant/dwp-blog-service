package com.devopswithprashant.service.blog.api.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateBlogRequest DTO Tests")
class CreateBlogRequestTests {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("should create request with all parameters via constructor")
        void shouldCreateWithAllParameters() {
            CreateBlogRequest request = new CreateBlogRequest(101L, "Test Title", "# Test Content");
            
            assertThat(request.getAuthorId()).isEqualTo(101L);
            assertThat(request.getTitle()).isEqualTo("Test Title");
            assertThat(request.getMarkdown()).isEqualTo("# Test Content");
        }

        @Test
        @DisplayName("should handle long author IDs correctly")
        void shouldHandleLongAuthorIds() {
            CreateBlogRequest request = new CreateBlogRequest(9999999L, "Title", "Content");
            assertThat(request.getAuthorId()).isEqualTo(9999999L);
        }

        @Test
        @DisplayName("should handle special characters in title")
        void shouldHandleSpecialCharactersInTitle() {
            String titleWithSpecialChars = "Blog: Special & Characters @ 2024";
            CreateBlogRequest request = new CreateBlogRequest(101L, titleWithSpecialChars, "Content");
            assertThat(request.getTitle()).isEqualTo(titleWithSpecialChars);
        }

        @Test
        @DisplayName("should handle multiline markdown content")
        void shouldHandleMultilineMarkdown() {
            String multilineMarkdown = "# Heading\n## Subheading\n\nParagraph content\n\n- List item 1\n- List item 2";
            CreateBlogRequest request = new CreateBlogRequest(101L, "Title", multilineMarkdown);
            assertThat(request.getMarkdown()).contains("\n").contains("# Heading");
        }

        @Test
        @DisplayName("should create empty request with no-arg constructor")
        void shouldCreateEmptyRequest() {
            CreateBlogRequest request = new CreateBlogRequest();
            assertThat(request).isNotNull();
        }
    }

    @Nested
    @DisplayName("AuthorId Getter/Setter Tests")
    class AuthorIdTests {
        
        @Test
        @DisplayName("should set and get authorId correctly")
        void shouldSetAndGetAuthorId() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setAuthorId(202L);
            assertThat(request.getAuthorId()).isEqualTo(202L);
        }

        @Test
        @DisplayName("should accept uuid author identity strings")
        void shouldAcceptUuidAuthorIdentityStrings() {
            CreateBlogRequest request = new CreateBlogRequest();
            String uuid = UUID.randomUUID().toString();

            request.setAuthorId(uuid);

            assertThat(request.getAuthorIdentity()).isEqualTo(uuid);
            assertThat(request.getAuthorId()).isNull();
        }

        @Test
        @DisplayName("should handle null authorId")
        void shouldHandleNullAuthorId() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setAuthorId(null);
            assertThat(request.getAuthorId()).isNull();
        }

        @Test
        @DisplayName("should replace authorId with new value")
        void shouldReplaceAuthorId() {
            CreateBlogRequest request = new CreateBlogRequest(101L, "Title", "Content");
            request.setAuthorId(303L);
            assertThat(request.getAuthorId()).isEqualTo(303L);
        }
    }

    @Nested
    @DisplayName("Title Getter/Setter Tests")
    class TitleTests {
        
        @Test
        @DisplayName("should set and get title correctly")
        void shouldSetAndGetTitle() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setTitle("New Title");
            assertThat(request.getTitle()).isEqualTo("New Title");
        }

        @Test
        @DisplayName("should handle empty title")
        void shouldHandleEmptyTitle() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setTitle("");
            assertThat(request.getTitle()).isEmpty();
        }

        @Test
        @DisplayName("should handle long title strings")
        void shouldHandleLongTitle() {
            String longTitle = "A".repeat(500);
            CreateBlogRequest request = new CreateBlogRequest();
            request.setTitle(longTitle);
            assertThat(request.getTitle()).hasSize(500);
        }

        @Test
        @DisplayName("should handle null title")
        void shouldHandleNullTitle() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setTitle(null);
            assertThat(request.getTitle()).isNull();
        }

        @Test
        @DisplayName("should replace title with new value")
        void shouldReplaceTitle() {
            CreateBlogRequest request = new CreateBlogRequest(101L, "Old Title", "Content");
            request.setTitle("New Title");
            assertThat(request.getTitle()).isEqualTo("New Title");
        }
    }

    @Nested
    @DisplayName("Markdown Getter/Setter Tests")
    class MarkdownTests {
        
        @Test
        @DisplayName("should set and get markdown correctly")
        void shouldSetAndGetMarkdown() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setMarkdown("# Heading");
            assertThat(request.getMarkdown()).isEqualTo("# Heading");
        }

        @Test
        @DisplayName("should handle empty markdown")
        void shouldHandleEmptyMarkdown() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setMarkdown("");
            assertThat(request.getMarkdown()).isEmpty();
        }

        @Test
        @DisplayName("should preserve markdown formatting")
        void shouldPreserveMarkdownFormatting() {
            String markdown = "# H1\n## H2\n**bold** *italic* `code`\n\n- item 1\n- item 2";
            CreateBlogRequest request = new CreateBlogRequest();
            request.setMarkdown(markdown);
            assertThat(request.getMarkdown()).isEqualTo(markdown);
        }

        @Test
        @DisplayName("should handle null markdown")
        void shouldHandleNullMarkdown() {
            CreateBlogRequest request = new CreateBlogRequest();
            request.setMarkdown(null);
            assertThat(request.getMarkdown()).isNull();
        }

        @Test
        @DisplayName("should replace markdown with new value")
        void shouldReplaceMarkdown() {
            CreateBlogRequest request = new CreateBlogRequest(101L, "Title", "Old Content");
            request.setMarkdown("New Content");
            assertThat(request.getMarkdown()).isEqualTo("New Content");
        }

        @Test
        @DisplayName("should handle markdown with special characters")
        void shouldHandleMarkdownWithSpecialCharacters() {
            String markdown = "Content with special chars: @#$%^&*()_+-=[]{}|;':\",./<>?";
            CreateBlogRequest request = new CreateBlogRequest();
            request.setMarkdown(markdown);
            assertThat(request.getMarkdown()).isEqualTo(markdown);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("should return string representation")
        void shouldReturnStringRepresentation() {
            CreateBlogRequest request = new CreateBlogRequest(101L, "Test", "Content");
            String toString = request.toString();
            assertThat(toString).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("should include class name in toString")
        void shouldIncludeClassNameInToString() {
            CreateBlogRequest request = new CreateBlogRequest();
            String toString = request.toString();
            assertThat(toString).contains("CreateBlogRequest");
        }

        @Test
        @DisplayName("should include field values in toString")
        void shouldIncludeFieldValuesInToString() {
            CreateBlogRequest request = new CreateBlogRequest(101L, "My Title", "My Content");
            String toString = request.toString();
            assertThat(toString).contains("101").contains("My Title").contains("My Content");
        }
    }

    @Nested
    @DisplayName("Integration/Scenario Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("should allow complete workflow: create -> modify -> read")
        void shouldAllowCompleteWorkflow() {
            // Create
            CreateBlogRequest request = new CreateBlogRequest(101L, "Original Title", "Original Content");
            
            // Modify
            request.setTitle("Modified Title");
            request.setMarkdown("Modified Content");
            
            // Read and verify
            assertThat(request.getAuthorId()).isEqualTo(101L);
            assertThat(request.getTitle()).isEqualTo("Modified Title");
            assertThat(request.getMarkdown()).isEqualTo("Modified Content");
        }

        @Test
        @DisplayName("should handle full request lifecycle")
        void shouldHandleFullLifecycle() {
            CreateBlogRequest request = new CreateBlogRequest();
            
            // Set all fields progressively
            request.setAuthorId(505L);
            request.setTitle("Complete Blog");
            request.setMarkdown("## Introduction\n\nThis is the content.");
            
            // Verify all fields are set correctly
            assertThat(request.getAuthorId()).isEqualTo(505L);
            assertThat(request.getTitle()).isEqualTo("Complete Blog");
            assertThat(request.getMarkdown()).contains("## Introduction");
        }

        @Test
        @DisplayName("should maintain data consistency through multiple operations")
        void shouldMaintainDataConsistency() {
            CreateBlogRequest request = new CreateBlogRequest(100L, "Title 1", "Content 1");
            
            // First modification
            request.setTitle("Title 2");
            assertThat(request.getAuthorId()).isEqualTo(100L);
            assertThat(request.getMarkdown()).isEqualTo("Content 1");
            
            // Second modification
            request.setMarkdown("Content 2");
            assertThat(request.getAuthorId()).isEqualTo(100L);
            assertThat(request.getTitle()).isEqualTo("Title 2");
        }
    }
}
