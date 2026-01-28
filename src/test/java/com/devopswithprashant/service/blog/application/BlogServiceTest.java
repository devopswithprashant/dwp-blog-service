package com.devopswithprashant.service.blog.application;

import com.devopswithprashant.service.blog.domain.BlogPost;
import com.devopswithprashant.service.blog.domain.BlogPostContent;
import com.devopswithprashant.service.blog.domain.PostStatus;
import com.devopswithprashant.service.blog.exception.BlogNotFoundException;
import com.devopswithprashant.service.blog.infrastructure.repository.BlogPostContentRepository;
import com.devopswithprashant.service.blog.infrastructure.repository.BlogPostRepository;
import com.devopswithprashant.service.blog.api.dto.BlogContentResponse;
import com.devopswithprashant.service.blog.api.dto.BlogMetadataResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @Mock
    private BlogPostRepository postRepository;

    @Mock
    private BlogPostContentRepository contentRepository;

    @InjectMocks
    private BlogService blogService;

    /* =========================
       getMetadata() tests
       ========================= */

    @Test
    void getMetadata_shouldReturnMetadata_whenBlogExists() {
        // given
        Long blogId = 1L;

        BlogPost post = new BlogPost();
        post.setId(blogId);
        post.setAuthorId(10L);
        post.setTitle("Spring Boot Blog");
        post.setSlug("spring-boot-blog");
        post.setStatus(PostStatus.DRAFT);
        post.setCreatedAt(Instant.now());
        post.setPublishedAt(null);

        when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

        // when
        BlogMetadataResponse response = blogService.getMetadata(blogId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(blogId);
        assertThat(response.getTitle()).isEqualTo("Spring Boot Blog");
        assertThat(response.getStatus()).isEqualTo("DRAFT");

        verify(postRepository).findById(blogId);
        verifyNoInteractions(contentRepository);
    }

    @Test
    void getMetadata_shouldThrowBlogNotFoundException_whenBlogIdIsNull() {
        assertThatThrownBy(() -> blogService.getMetadata(null))
                .isInstanceOf(BlogNotFoundException.class)
                .hasMessageContaining("Blog not found");
    }

    @Test
    void getMetadata_shouldThrowBlogNotFoundException_whenBlogIdIsInvalid() {
        Long blogId = 0L;

        when(postRepository.findById(blogId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.getMetadata(blogId))
                .isInstanceOf(BlogNotFoundException.class)
                .hasMessageContaining("Blog not found");

        verify(postRepository).findById(blogId);
    }

    /* =========================
       getContent() tests
       ========================= */

    @Test
    void getContent_shouldReturnContent_whenBlogExists() {
        // given
        Long blogId = 2L;

        BlogPostContent content = new BlogPostContent();
        content.setPostId(blogId);
        content.setContent("# Hello Markdown");
        content.setFormat("MARKDOWN");

        when(contentRepository.findByPostId(blogId)).thenReturn(Optional.of(content));

        // when
        BlogContentResponse response = blogService.getContent(blogId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isEqualTo(blogId);
        assertThat(response.getContent()).contains("Hello");
        assertThat(response.getFormat()).isEqualTo("MARKDOWN");

        verify(contentRepository).findByPostId(blogId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void getContent_shouldThrowBlogNotFoundException_whenBlogIdIsNull() {
        assertThatThrownBy(() -> blogService.getContent(null))
                .isInstanceOf(BlogNotFoundException.class)
                .hasMessageContaining("Blog not found");
    }

    @Test
    void getContent_shouldThrowBlogNotFoundException_whenBlogIdIsInvalid() {
        Long blogId = 0L;

        when(contentRepository.findByPostId(blogId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.getContent(blogId))
                .isInstanceOf(BlogNotFoundException.class)
                .hasMessageContaining("Blog not found");

        verify(contentRepository).findByPostId(blogId);
    }
}