package com.devopswithprashant.service.blog.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BlogPostContent Domain Entity Tests")
class BlogPostContentTests {

    @Nested
    @DisplayName("All-Args Constructor Tests")
    class AllArgsConstructorTests {
        
        @Test
        @DisplayName("should create BlogPostContent with all parameters")
        void shouldCreateWithAllParameters() {
            BlogPostContent content = new BlogPostContent(100L, "# Content", "markdown");
            
            assertThat(content.getPostId()).isEqualTo(100L);
            assertThat(content.getContent()).isEqualTo("# Content");
            assertThat(content.getFormat()).isEqualTo("markdown");
        }

        @Test
        @DisplayName("should handle long content strings")
        void shouldHandleLongContent() {
            String longContent = "# Title\n" + "A".repeat(50000);
            BlogPostContent content = new BlogPostContent(100L, longContent, "markdown");
            
            assertThat(content.getContent()).startsWith("# Title");
            assertThat(content.getContent()).hasSize(longContent.length());
        }

        @Test
        @DisplayName("should preserve all field values exactly")
        void shouldPreserveAllFieldValues() {
            String complexMarkdown = "## Section\n\n**Bold** and *italic* text\n\n```code block```\n\n- List item";
            BlogPostContent content = new BlogPostContent(999L, complexMarkdown, "markdown");
            
            assertThat(content.getPostId()).isEqualTo(999L);
            assertThat(content.getContent()).isEqualTo(complexMarkdown);
            assertThat(content.getFormat()).isEqualTo("markdown");
        }

        @Test
        @DisplayName("should handle different content formats")
        void shouldHandleDifferentFormats() {
            BlogPostContent md = new BlogPostContent(1L, "# Markdown", "markdown");
            BlogPostContent html = new BlogPostContent(2L, "<h1>HTML</h1>", "html");
            BlogPostContent plaintext = new BlogPostContent(3L, "Plain text content", "plaintext");
            
            assertThat(md.getFormat()).isEqualTo("markdown");
            assertThat(html.getFormat()).isEqualTo("html");
            assertThat(plaintext.getFormat()).isEqualTo("plaintext");
        }
    }

    @Nested
    @DisplayName("No-Args Constructor Tests")
    class NoArgsConstructorTests {
        
        @Test
        @DisplayName("should create empty BlogPostContent with no-arg constructor")
        void shouldCreateEmpty() {
            BlogPostContent content = new BlogPostContent();
            assertThat(content).isNotNull();
        }

        @Test
        @DisplayName("should allow setting fields after no-arg construction")
        void shouldAllowSettingFields() {
            BlogPostContent content = new BlogPostContent();
            
            content.setPostId(100L);
            content.setContent("Test content");
            content.setFormat("markdown");
            
            assertThat(content.getPostId()).isEqualTo(100L);
            assertThat(content.getContent()).isEqualTo("Test content");
            assertThat(content.getFormat()).isEqualTo("markdown");
        }
    }

    @Nested
    @DisplayName("PostId Getter/Setter Tests")
    class PostIdTests {
        
        @Test
        @DisplayName("should set and get postId correctly")
        void shouldSetAndGetPostId() {
            BlogPostContent content = new BlogPostContent();
            content.setPostId(200L);
            assertThat(content.getPostId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("should handle null postId")
        void shouldHandleNullPostId() {
            BlogPostContent content = new BlogPostContent();
            content.setPostId(null);
            assertThat(content.getPostId()).isNull();
        }

        @Test
        @DisplayName("should replace postId with new value")
        void shouldReplacePostId() {
            BlogPostContent content = new BlogPostContent(100L, "Content", "markdown");
            content.setPostId(200L);
            assertThat(content.getPostId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("should handle large postId values")
        void shouldHandleLargePostIds() {
            BlogPostContent content = new BlogPostContent();
            content.setPostId(9999999999L);
            assertThat(content.getPostId()).isEqualTo(9999999999L);
        }
    }

    @Nested
    @DisplayName("Content Getter/Setter Tests")
    class ContentTests {
        
        @Test
        @DisplayName("should set and get content correctly")
        void shouldSetAndGetContent() {
            BlogPostContent content = new BlogPostContent();
            content.setContent("# New Content");
            assertThat(content.getContent()).isEqualTo("# New Content");
        }

        @Test
        @DisplayName("should handle empty content")
        void shouldHandleEmptyContent() {
            BlogPostContent content = new BlogPostContent();
            content.setContent("");
            assertThat(content.getContent()).isEmpty();
        }

        @Test
        @DisplayName("should handle null content")
        void shouldHandleNullContent() {
            BlogPostContent content = new BlogPostContent();
            content.setContent(null);
            assertThat(content.getContent()).isNull();
        }

        @Test
        @DisplayName("should preserve markdown formatting in content")
        void shouldPreserveMarkdownFormatting() {
            String markdown = "# Heading\n## Subheading\n\n**Bold** *Italic*\n\n- Item 1\n- Item 2\n\n```code```";
            BlogPostContent content = new BlogPostContent();
            content.setContent(markdown);
            assertThat(content.getContent()).isEqualTo(markdown);
        }

        @Test
        @DisplayName("should handle multiline content with special characters")
        void shouldHandleSpecialCharacters() {
            String content = "Text with special: @#$%^&*()_+-=[]{}|;':\",./<>?\n\nUnicode: é ñ ü 中文";
            BlogPostContent blogContent = new BlogPostContent();
            blogContent.setContent(content);
            assertThat(blogContent.getContent()).isEqualTo(content);
        }

        @Test
        @DisplayName("should replace content with new value")
        void shouldReplaceContent() {
            BlogPostContent content = new BlogPostContent(100L, "Old content", "markdown");
            content.setContent("New content");
            assertThat(content.getContent()).isEqualTo("New content");
        }
    }

    @Nested
    @DisplayName("Format Getter/Setter Tests")
    class FormatTests {
        
        @Test
        @DisplayName("should set and get format correctly")
        void shouldSetAndGetFormat() {
            BlogPostContent content = new BlogPostContent();
            content.setFormat("html");
            assertThat(content.getFormat()).isEqualTo("html");
        }

        @Test
        @DisplayName("should handle different format types")
        void shouldHandleDifferentFormats() {
            BlogPostContent content = new BlogPostContent();
            
            content.setFormat("markdown");
            assertThat(content.getFormat()).isEqualTo("markdown");
            
            content.setFormat("html");
            assertThat(content.getFormat()).isEqualTo("html");
            
            content.setFormat("plaintext");
            assertThat(content.getFormat()).isEqualTo("plaintext");
        }

        @Test
        @DisplayName("should handle null format")
        void shouldHandleNullFormat() {
            BlogPostContent content = new BlogPostContent();
            content.setFormat(null);
            assertThat(content.getFormat()).isNull();
        }

        @Test
        @DisplayName("should replace format with new value")
        void shouldReplaceFormat() {
            BlogPostContent content = new BlogPostContent(100L, "Content", "markdown");
            content.setFormat("html");
            assertThat(content.getFormat()).isEqualTo("html");
        }

        @Test
        @DisplayName("should handle format value case sensitivity")
        void shouldHandleFormatCaseSensitivity() {
            BlogPostContent content1 = new BlogPostContent();
            content1.setFormat("Markdown");
            
            BlogPostContent content2 = new BlogPostContent();
            content2.setFormat("MARKDOWN");
            
            assertThat(content1.getFormat()).isEqualTo("Markdown");
            assertThat(content2.getFormat()).isEqualTo("MARKDOWN");
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("should return string representation")
        void shouldReturnStringRepresentation() {
            BlogPostContent content = new BlogPostContent(100L, "Content", "markdown");
            String str = content.toString();
            assertThat(str).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("should include class name in toString")
        void shouldIncludeClassName() {
            BlogPostContent content = new BlogPostContent();
            assertThat(content.toString()).contains("BlogPostContent");
        }
    }

    @Nested
    @DisplayName("Integration/Scenario Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("should handle complete content lifecycle")
        void shouldHandleCompleteContentLifecycle() {
            // Create content
            BlogPostContent content = new BlogPostContent(100L, "Initial content", "markdown");
            
            // Update content
            content.setContent("Updated content with more details\n\n## Section\n\nMore info");
            
            // Change format (if needed for conversion)
            content.setFormat("html");
            
            // Verify final state
            assertThat(content.getPostId()).isEqualTo(100L);
            assertThat(content.getContent()).contains("## Section");
            assertThat(content.getFormat()).isEqualTo("html");
        }

        @Test
        @DisplayName("should support multiple content updates for same post")
        void shouldSupportMultipleUpdates() {
            BlogPostContent content = new BlogPostContent();
            content.setPostId(100L);
            content.setFormat("markdown");
            
            // First draft
            content.setContent("# First Draft\n\nInitial content");
            assertThat(content.getContent()).contains("First Draft");
            
            // First revision
            content.setContent("# First Revision\n\nRevised content");
            assertThat(content.getContent()).contains("First Revision");
            
            // Second revision
            content.setContent("# Final Version\n\nFinal content ready");
            assertThat(content.getContent()).contains("Final Version");
            
            // Verify post ID remains constant
            assertThat(content.getPostId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("should handle content with various markdown elements")
        void shouldHandleComplexMarkdown() {
            String complexMarkdown = "# Main Title\n\n## Introduction\n\n" +
                "This is a paragraph with **bold** and *italic* text.\n\n" +
                "### Lists\n\n" +
                "- Item 1\n" +
                "- Item 2\n" +
                "  - Nested item\n\n" +
                "### Code\n\n" +
                "```java\npublic class Example {}\n```\n\n" +
                "### Links\n\n" +
                "[Link text](https://example.com)";
            
            BlogPostContent content = new BlogPostContent(100L, complexMarkdown, "markdown");
            
            assertThat(content.getContent()).contains("# Main Title");
            assertThat(content.getContent()).contains("**bold**");
            assertThat(content.getContent()).contains("- Item 1");
            assertThat(content.getContent()).contains("```java");
            assertThat(content.getContent()).contains("[Link text]");
        }
    }
}
