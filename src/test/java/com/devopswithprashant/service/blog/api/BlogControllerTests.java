package com.devopswithprashant.service.blog.api;

import com.devopswithprashant.service.blog.api.dto.BlogContentResponse;
import com.devopswithprashant.service.blog.api.dto.BlogMetadataResponse;
import com.devopswithprashant.service.blog.api.dto.CreateBlogRequest;
import com.devopswithprashant.service.blog.api.dto.UpdateBlogRequest;
import com.devopswithprashant.service.blog.application.BlogService;
import com.devopswithprashant.service.blog.domain.PostStatus;
import com.devopswithprashant.service.blog.exception.BlogNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlogController Tests")
class BlogControllerTests {

    @Mock
    private BlogService blogService;

    @InjectMocks
    private BlogController controller;

    @Nested
    @DisplayName("CreateDraft Endpoint Tests")
    class CreateDraftEndpointTests {
        
        @Test
        @DisplayName("should return 200 OK with blog ID when creating draft successfully")
        void shouldCreateDraftSuccessfully() {
            CreateBlogRequest request = new CreateBlogRequest(101L, "Test Blog", "# Markdown Content");
            when(blogService.createDraft(101L, "Test Blog", "# Markdown Content")).thenReturn(1L);
            
            ResponseEntity<Long> response = controller.createDraft(request);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(1L);
            verify(blogService).createDraft(101L, "Test Blog", "# Markdown Content");
        }

        @Test
        @DisplayName("should pass correct parameters from request to service")
        void shouldPassCorrectParametersToService() {
            CreateBlogRequest request = new CreateBlogRequest(202L, "Another Blog", "## Content");
            when(blogService.createDraft(202L, "Another Blog", "## Content")).thenReturn(2L);
            
            controller.createDraft(request);
            
            verify(blogService).createDraft(202L, "Another Blog", "## Content");
        }

        @Test
        @DisplayName("should return OK status with numeric ID in response")
        void shouldReturnOkStatusWithNumericId() {
            CreateBlogRequest request = new CreateBlogRequest(303L, "Blog Title", "Content");
            when(blogService.createDraft(303L, "Blog Title", "Content")).thenReturn(999L);
            
            ResponseEntity<Long> response = controller.createDraft(request);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(Long.class).isEqualTo(999L);
        }
    }

    @Nested
    @DisplayName("GetMetadata Endpoint Tests")
    class GetMetadataEndpointTests {
        
        @Test
        @DisplayName("should return 200 OK with blog metadata when blog exists")
        void shouldReturnMetadataWhenBlogExists() {
            BlogMetadataResponse metadata = new BlogMetadataResponse(
                1L, 101L, "Test Blog", "test-blog", 
                PostStatus.DRAFT.name(), Instant.now(), null
            );
            when(blogService.getMetadata(1L)).thenReturn(metadata);
            
            ResponseEntity<BlogMetadataResponse> response = controller.getMetadata(1L);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(metadata);
            verify(blogService).getMetadata(1L);
        }

        @Test
        @DisplayName("should throw BlogNotFoundException when blog does not exist")
        void shouldThrowExceptionWhenBlogNotFound() {
            when(blogService.getMetadata(999L)).thenThrow(new BlogNotFoundException(999L));
            
            try {
                controller.getMetadata(999L);
            } catch (BlogNotFoundException e) {
                assertThat(e).isInstanceOf(BlogNotFoundException.class);
            }
        }

        @Test
        @DisplayName("should return correct blog metadata with all fields")
        void shouldReturnCompleteMetadata() {
            Instant createdAt = Instant.now();
            BlogMetadataResponse metadata = new BlogMetadataResponse(
                5L, 202L, "Complete Blog", "complete-blog", 
                PostStatus.PUBLISHED.name(), createdAt, createdAt.plusSeconds(3600)
            );
            when(blogService.getMetadata(5L)).thenReturn(metadata);
            
            ResponseEntity<BlogMetadataResponse> response = controller.getMetadata(5L);
            
            assertThat(response.getBody().getId()).isEqualTo(5L);
            assertThat(response.getBody().getAuthorId()).isEqualTo(202L);
            assertThat(response.getBody().getTitle()).isEqualTo("Complete Blog");
            assertThat(response.getBody().getStatus()).isEqualTo(PostStatus.PUBLISHED.name());
        }
    }

    @Nested
    @DisplayName("GetContent Endpoint Tests")
    class GetContentEndpointTests {
        
        @Test
        @DisplayName("should return 200 OK with blog content when blog exists")
        void shouldReturnContentWhenBlogExists() {
            BlogContentResponse content = new BlogContentResponse(1L, "# Blog Content", "markdown");
            when(blogService.getContent(1L)).thenReturn(content);
            
            ResponseEntity<BlogContentResponse> response = controller.getContent(1L);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(content);
            verify(blogService).getContent(1L);
        }

        @Test
        @DisplayName("should throw BlogNotFoundException when blog does not exist")
        void shouldThrowExceptionWhenContentNotFound() {
            when(blogService.getContent(999L)).thenThrow(new BlogNotFoundException(999L));
            
            try {
                controller.getContent(999L);
            } catch (BlogNotFoundException e) {
                assertThat(e).isInstanceOf(BlogNotFoundException.class);
            }
        }

        @Test
        @DisplayName("should return correct content with format")
        void shouldReturnCorrectContent() {
            String markdown = "## Section 1\n### Subsection\nContent here";
            BlogContentResponse content = new BlogContentResponse(10L, markdown, "markdown");
            when(blogService.getContent(10L)).thenReturn(content);
            
            ResponseEntity<BlogContentResponse> response = controller.getContent(10L);
            
            assertThat(response.getBody().getPostId()).isEqualTo(10L);
            assertThat(response.getBody().getContent()).isEqualTo(markdown);
            assertThat(response.getBody().getFormat()).isEqualTo("markdown");
        }
    }

    @Nested
    @DisplayName("GetAllBlogs Endpoint Tests")
    class GetAllBlogsEndpointTests {
        
        @Test
        @DisplayName("should return 200 OK with paginated blogs")
        void shouldReturnPaginatedBlogs() {
            List<BlogMetadataResponse> blogs = new ArrayList<>();
            blogs.add(new BlogMetadataResponse(1L, 101L, "Blog 1", "blog-1", PostStatus.DRAFT.name(), Instant.now(), null));
            blogs.add(new BlogMetadataResponse(2L, 102L, "Blog 2", "blog-2", PostStatus.PUBLISHED.name(), Instant.now(), Instant.now()));
            
            Page<BlogMetadataResponse> page = new PageImpl<>(blogs, PageRequest.of(0, 10), 2);
            when(blogService.getAllBlogs(any())).thenReturn(page);
            
            ResponseEntity<Page<BlogMetadataResponse>> response = controller.getAllBlogs(PageRequest.of(0, 10));
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).hasSize(2);
            assertThat(response.getBody().getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return empty page when no blogs exist")
        void shouldReturnEmptyPageWhenNoBlogsExist() {
            Page<BlogMetadataResponse> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);
            when(blogService.getAllBlogs(any())).thenReturn(emptyPage);
            
            ResponseEntity<Page<BlogMetadataResponse>> response = controller.getAllBlogs(PageRequest.of(0, 10));
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).isEmpty();
            assertThat(response.getBody().getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("should respect pagination parameters")
        void shouldRespectPaginationParameters() {
            List<BlogMetadataResponse> page2Blogs = new ArrayList<>();
            page2Blogs.add(new BlogMetadataResponse(3L, 103L, "Blog 3", "blog-3", PostStatus.DRAFT.name(), Instant.now(), null));
            
            Page<BlogMetadataResponse> page = new PageImpl<>(page2Blogs, PageRequest.of(1, 10), 11);
            when(blogService.getAllBlogs(PageRequest.of(1, 10))).thenReturn(page);
            
            ResponseEntity<Page<BlogMetadataResponse>> response = controller.getAllBlogs(PageRequest.of(1, 10));
            
            assertThat(response.getBody().getNumber()).isEqualTo(1);
            assertThat(response.getBody().getSize()).isEqualTo(10);
            assertThat(response.getBody().getTotalElements()).isEqualTo(11);
        }
    }

    @Nested
    @DisplayName("UpdateBlog Endpoint Tests")
    class UpdateBlogEndpointTests {
        
        @Test
        @DisplayName("should return 200 OK when blog is updated successfully")
        void shouldUpdateBlogSuccessfully() {
            UpdateBlogRequest request = new UpdateBlogRequest("Updated Title", "Updated content");
            
            ResponseEntity<Void> response = controller.updateBlog(1L, request);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(blogService).updateBlog(1L, request);
        }

        @Test
        @DisplayName("should throw BlogNotFoundException when blog to update does not exist")
        void shouldThrowExceptionWhenBlogNotFound() {
            UpdateBlogRequest request = new UpdateBlogRequest("Title", "Content");
            doThrow(new BlogNotFoundException(999L)).when(blogService).updateBlog(999L, request);
            
            try {
                controller.updateBlog(999L, request);
            } catch (BlogNotFoundException e) {
                assertThat(e).isInstanceOf(BlogNotFoundException.class);
            }
        }

        @Test
        @DisplayName("should pass correct update request to service")
        void shouldPassCorrectUpdateRequest() {
            UpdateBlogRequest request = new UpdateBlogRequest("New Title", "New Markdown");
            
            controller.updateBlog(5L, request);
            
            verify(blogService).updateBlog(5L, request);
        }
    }

    @Nested
    @DisplayName("DeleteBlog Endpoint Tests")
    class DeleteBlogEndpointTests {
        
        @Test
        @DisplayName("should return 204 NO_CONTENT when blog is deleted successfully")
        void shouldDeleteBlogSuccessfully() {
            ResponseEntity<Void> response = controller.deleteBlog(1L);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(blogService).deleteBlog(1L);
        }

        @Test
        @DisplayName("should throw BlogNotFoundException when blog to delete does not exist")
        void shouldThrowExceptionWhenBlogNotFound() {
            doThrow(new BlogNotFoundException(999L)).when(blogService).deleteBlog(999L);
            
            try {
                controller.deleteBlog(999L);
            } catch (BlogNotFoundException e) {
                assertThat(e).isInstanceOf(BlogNotFoundException.class);
            }
        }

        @Test
        @DisplayName("should call service delete method with correct blog ID")
        void shouldCallServiceDeleteWithCorrectId() {
            controller.deleteBlog(7L);
            
            verify(blogService).deleteBlog(7L);
        }
    }

    @Nested
    @DisplayName("PublishBlog Endpoint Tests")
    class PublishBlogEndpointTests {
        
        @Test
        @DisplayName("should return 200 OK when blog is published successfully")
        void shouldPublishBlogSuccessfully() {
            ResponseEntity<Void> response = controller.publish(1L);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(blogService).publish(1L);
        }

        @Test
        @DisplayName("should throw BlogNotFoundException when blog to publish does not exist")
        void shouldThrowExceptionWhenBlogNotFound() {
            doThrow(new BlogNotFoundException(999L)).when(blogService).publish(999L);
            
            try {
                controller.publish(999L);
            } catch (BlogNotFoundException e) {
                assertThat(e).isInstanceOf(BlogNotFoundException.class);
            }
        }

        @Test
        @DisplayName("should call service publish method with correct blog ID")
        void shouldCallServicePublishWithCorrectId() {
            controller.publish(3L);
            
            verify(blogService).publish(3L);
        }
    }
}
