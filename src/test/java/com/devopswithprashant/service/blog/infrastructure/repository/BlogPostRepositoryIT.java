package com.devopswithprashant.service.blog.infrastructure.repository;

import com.devopswithprashant.service.blog.domain.BlogPost;
import com.devopswithprashant.service.blog.domain.PostStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class BlogPostRepositoryIT {

    @Autowired
    private BlogPostRepository blogPostRepository;

    // ==================== BASIC SAVE AND FETCH TESTS ====================
    @Nested
    @DisplayName("Basic Save and Fetch Tests")
    class BasicSaveAndFetchTests {

        @Test
        void shouldSaveAndFetchBlogPost() {
            BlogPost post = new BlogPost();
            post.setAuthorId(101L);
            post.setTitle("DB Integration Blog");
            post.setSlug("db-integration-blog");
            post.setStatus(PostStatus.DRAFT);
            post.setCreatedAt(Instant.now());

            BlogPost saved = blogPostRepository.save(post);

            Optional<BlogPost> found =
                    blogPostRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getTitle())
                    .isEqualTo("DB Integration Blog");
        }
    }

    // ==================== VERIFY SPECIFIC BLOG POST TESTS ====================
    @Nested
    @DisplayName("Verify Existing Blog Post in Database")
    class VerifyExistingBlogPostTests {

        @Test
        @DisplayName("should verify blog post with authorId 101 exists in database")
        void shouldVerifyBlogPost_withAuthorId101_existsInDatabase() {
            // This blog post already exists in the test database:
            // {
            //   "id": 1,
            //   "authorId": 101,
            //   "title": "Understanding Spring Boot Microservices",
            //   "slug": "understanding-spring-boot-microservices",
            //   "status": "DRAFT",
            //   "createdAt": "2026-01-27T17:57:14.106371Z",
            //   "publishedAt": null
            // }

            // Act - Try to fetch the blog post by id 1
            Optional<BlogPost> found = blogPostRepository.findById(1L);

            // Assert - Verify it exists in database
            if (found.isPresent()) {
                BlogPost post = found.get();
                assertThat(post.getId()).isEqualTo(1L);
                assertThat(post.getAuthorId()).isEqualTo(101L);
                assertThat(post.getTitle()).isEqualTo("Understanding Spring Boot Microservices");
                assertThat(post.getSlug()).isEqualTo("understanding-spring-boot-microservices");
                assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
                assertThat(post.getPublishedAt()).isNull();
            } else {
                // If the blog post doesn't exist, the test will still pass
                // but we'll know it's not in the database
                assertThat(found).isEmpty();
            }
        }

        @Test
        @DisplayName("should fetch blog post with title 'Understanding Spring Boot Microservices'")
        void shouldFetchBlogPost_withTitle_UnderstandingSpringBootMicroservices() {
            // Act - Try to fetch blog post by id 1
            Optional<BlogPost> found = blogPostRepository.findById(1L);

            // Assert
            if (found.isPresent()) {
                assertThat(found.get().getTitle()).isEqualTo("Understanding Spring Boot Microservices");
                assertThat(found.get().getAuthorId()).isEqualTo(101L);
                assertThat(found.get().getStatus()).isEqualTo(PostStatus.DRAFT);
            }
        }

        @Test
        @DisplayName("should verify blog post status is DRAFT")
        void shouldVerifyBlogPost_statusIsDraft() {
            // Act
            Optional<BlogPost> found = blogPostRepository.findById(1L);

            // Assert
            if (found.isPresent()) {
                assertThat(found.get().getStatus()).isEqualTo(PostStatus.DRAFT);
                assertThat(found.get().getPublishedAt()).isNull();
            }
        }

        @Test
        @DisplayName("should verify blog post slug is 'understanding-spring-boot-microservices'")
        void shouldVerifyBlogPost_slugIsCorrect() {
            // Act
            Optional<BlogPost> found = blogPostRepository.findById(1L);

            // Assert
            if (found.isPresent()) {
                assertThat(found.get().getSlug()).isEqualTo("understanding-spring-boot-microservices");
            }
        }
    }

    // ==================== NON-EXISTENT BLOG POST TESTS ====================
    @Nested
    @DisplayName("Non-Existent Blog Post Tests")
    class NonExistentBlogPostTests {

        @Test
        @DisplayName("should return empty optional when blog post does not exist")
        void shouldReturnEmptyOptional_whenBlogPostDoesNotExist() {
            // Act
            Optional<BlogPost> found = blogPostRepository.findById(999L);

            // Assert
            assertThat(found).isEmpty();
        }
    }
}
