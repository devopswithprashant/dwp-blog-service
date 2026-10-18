package com.devopswithprashant.service.blog.application;

import com.devopswithprashant.service.blog.domain.BlogPost;
import com.devopswithprashant.service.blog.domain.BlogPostContent;
import com.devopswithprashant.service.blog.domain.BlogPostVersion;
import com.devopswithprashant.service.blog.domain.PostStatus;
import com.devopswithprashant.service.blog.exception.BlogNotFoundException;
import com.devopswithprashant.service.blog.infrastructure.repository.BlogPostContentRepository;
import com.devopswithprashant.service.blog.infrastructure.repository.BlogPostRepository;
import com.devopswithprashant.service.blog.infrastructure.repository.BlogPostVersionRepository;
import com.devopswithprashant.service.blog.infrastructure.metrics.BlogMetrics;
import com.devopswithprashant.service.blog.api.dto.BlogContentResponse;
import com.devopswithprashant.service.blog.api.dto.BlogMetadataResponse;
import com.devopswithprashant.service.blog.api.dto.UpdateBlogRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
//import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    private static final UUID AUTHOR_UUID = UUID.fromString("96c8bbf1-22b1-49c6-8a0d-e31f9f9df31d");

    @Mock
    private BlogPostRepository postRepository;

    @Mock
    private BlogPostContentRepository contentRepository;

    @Mock
    private BlogPostVersionRepository versionRepository;

    @Mock
    private BlogMetrics blogMetrics;

    @InjectMocks
    private BlogService blogService;

    // Counter for generating unique IDs
    private final AtomicLong idCounter = new AtomicLong(0);

    @BeforeEach
    void stubMetricsToExecuteCallbacks() {
        lenient().doAnswer(invocation -> {
            Runnable action = invocation.getArgument(1);
            action.run();
            return null;
        }).when(blogMetrics).recordTimed(anyString(), any(Runnable.class));

        lenient().doAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(1);
            return supplier.get();
        }).when(blogMetrics).recordTimed(anyString(), any(Supplier.class));
    }

    // Helper method to setup postRepository.save() mock with ID assignment
    private void setupPostRepositorySaveMock() {
        doAnswer(invocation -> {
            BlogPost post = invocation.getArgument(0);
            if (post.getId() == null) {
                post.setId(idCounter.incrementAndGet());
            }
            return post;
        }).when(postRepository).save(any(BlogPost.class));
    }

    // ==================== CREATE DRAFT TESTS ====================
    @Nested
    @DisplayName("CreateDraft Tests")
    class CreateDraftTests {

        @Test
        @DisplayName("should create draft blog with valid parameters")
        void shouldCreateDraftBlog_withValidParameters() {
            String title = "New Blog Post";
            String markdown = "# Hello World\nThis is content";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            Long blogId = blogService.createDraft(AUTHOR_UUID, title, markdown);

            assertThat(blogId).isNotNull();
            verify(postRepository).save(any(BlogPost.class));
            verify(contentRepository).save(any(BlogPostContent.class));
            verify(versionRepository).save(any(BlogPostVersion.class));
        }

        @Test
        @DisplayName("should set draft status when creating blog")
        void shouldSetDraftStatus_whenCreatingBlog() {
            String title = "Draft Article";
            String markdown = "Content";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            blogService.createDraft(AUTHOR_UUID, title, markdown);

            ArgumentCaptor<BlogPost> postCaptor = ArgumentCaptor.forClass(BlogPost.class);
            verify(postRepository).save(postCaptor.capture());
            assertThat(postCaptor.getValue().getStatus()).isEqualTo(PostStatus.DRAFT);
        }

        @Test
        @DisplayName("should associate the authenticated author's UUID")
        void shouldSetAuthorUuidCorrectly() {
            String title = "Author Blog";
            String markdown = "By author";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            blogService.createDraft(AUTHOR_UUID, title, markdown);

            ArgumentCaptor<BlogPost> postCaptor = ArgumentCaptor.forClass(BlogPost.class);
            verify(postRepository).save(postCaptor.capture());
            assertThat(postCaptor.getValue().getAuthorUuid()).isEqualTo(AUTHOR_UUID);
        }

        @Test
        @DisplayName("should associate the provided authenticated UUID")
        void shouldAssociateAuthenticatedUuid() {
            String title = "Uuid Author Blog";
            String markdown = "By uuid author";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            blogService.createDraft(AUTHOR_UUID, title, markdown);

            ArgumentCaptor<BlogPost> postCaptor = ArgumentCaptor.forClass(BlogPost.class);
            verify(postRepository).save(postCaptor.capture());
            assertThat(postCaptor.getValue().getAuthorUuid()).isEqualTo(AUTHOR_UUID);
        }

        @Test
        @DisplayName("should reject draft creation without an authenticated UUID")
        void shouldRejectMissingAuthorUuid() {
            assertThatThrownBy(() -> blogService.createDraft(null, "Title", "Content"))
                    .isInstanceOf(NullPointerException.class);
            verify(postRepository, never()).save(any(BlogPost.class));
        }

        @Test
        @DisplayName("should generate slug from title")
        void shouldGenerateSlugFromTitle() {
            String title = "Spring Boot Best Practices";
            String markdown = "Content here";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            blogService.createDraft(AUTHOR_UUID, title, markdown);

            ArgumentCaptor<BlogPost> postCaptor = ArgumentCaptor.forClass(BlogPost.class);
            verify(postRepository).save(postCaptor.capture());
            assertThat(postCaptor.getValue().getSlug()).isNotEmpty();
        }

        @Test
        @DisplayName("should save content with markdown format")
        void shouldSaveContentWithMarkdownFormat() {
            String title = "Markdown Blog";
            String markdown = "# Title\n**Bold**";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            blogService.createDraft(AUTHOR_UUID, title, markdown);

            ArgumentCaptor<BlogPostContent> contentCaptor = ArgumentCaptor.forClass(BlogPostContent.class);
            verify(contentRepository).save(contentCaptor.capture());
            assertThat(contentCaptor.getValue().getFormat()).isEqualTo("MARKDOWN");
        }

        @Test
        @DisplayName("should create initial version entry")
        void shouldCreateInitialVersionEntry() {
            String title = "Version Test";
            String markdown = "Version content";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            blogService.createDraft(AUTHOR_UUID, title, markdown);

            ArgumentCaptor<BlogPostVersion> versionCaptor = ArgumentCaptor.forClass(BlogPostVersion.class);
            verify(versionRepository).save(versionCaptor.capture());
            assertThat(versionCaptor.getValue().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("should set creation timestamp")
        void shouldSetCreationTimestamp() {
            String title = "Timestamped Blog";
            String markdown = "Timestamped";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            blogService.createDraft(AUTHOR_UUID, title, markdown);

            ArgumentCaptor<BlogPost> postCaptor = ArgumentCaptor.forClass(BlogPost.class);
            verify(postRepository).save(postCaptor.capture());
            assertThat(postCaptor.getValue().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should handle empty markdown content")
        void shouldHandleEmptyMarkdownContent() {
            String title = "Empty Content Blog";
            String markdown = "";

            setupPostRepositorySaveMock();
            when(contentRepository.save(any(BlogPostContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(versionRepository.findByPostIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());

            Long blogId = blogService.createDraft(AUTHOR_UUID, title, markdown);

            assertThat(blogId).isNotNull();
            verify(contentRepository).save(any(BlogPostContent.class));
        }
    }

    // ==================== GET METADATA TESTS ====================
    @Nested
    @DisplayName("GetMetadata Tests")
    class GetMetadataTests {

        @Test
        @DisplayName("should return metadata when blog exists")
        void shouldReturnMetadata_whenBlogExists() {
            Long blogId = 1L;
            BlogPost post = createBlogPost(blogId, "Spring Boot Blog", PostStatus.DRAFT);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            BlogMetadataResponse response = blogService.getMetadata(blogId);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(blogId);
            assertThat(response.getTitle()).isEqualTo("Spring Boot Blog");
            assertThat(response.getStatus()).isEqualTo("DRAFT");
            verify(postRepository).findById(blogId);
        }

        @Test
        @DisplayName("should throw exception when blog id is null")
        void shouldThrowException_whenBlogIdIsNull() {
            assertThatThrownBy(() -> blogService.getMetadata(null))
                    .isInstanceOf(BlogNotFoundException.class)
                    .hasMessageContaining("Blog not found");
        }

        @Test
        @DisplayName("should throw exception when blog id is invalid")
        void shouldThrowException_whenBlogIdIsInvalid() {
            Long blogId = 999L;
            when(postRepository.findById(blogId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> blogService.getMetadata(blogId))
                    .isInstanceOf(BlogNotFoundException.class)
                    .hasMessageContaining("Blog not found");

            verify(postRepository).findById(blogId);
        }

        @Test
        @DisplayName("should return published blog metadata")
        void shouldReturnPublishedBlogMetadata() {
            Long blogId = 2L;
            BlogPost post = createBlogPost(blogId, "Published Article", PostStatus.PUBLISHED);
            post.setPublishedAt(Instant.now());

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            BlogMetadataResponse response = blogService.getMetadata(blogId);

            assertThat(response.getStatus()).isEqualTo("PUBLISHED");
            assertThat(response.getPublishedAt()).isNotNull();
        }

        @Test
        @DisplayName("should return draft blog without published date")
        void shouldReturnDraftBlogWithoutPublishedDate() {
            Long blogId = 3L;
            BlogPost post = createBlogPost(blogId, "Draft Article", PostStatus.DRAFT);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            BlogMetadataResponse response = blogService.getMetadata(blogId);

            assertThat(response.getPublishedAt()).isNull();
        }

        @Test
        @DisplayName("should return correct author id in metadata")
        void shouldReturnCorrectAuthorIdInMetadata() {
            Long blogId = 4L;
            Long authorId = 12345L;
            BlogPost post = createBlogPost(blogId, "Test Blog", PostStatus.DRAFT);
            post.setAuthorId(authorId);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            BlogMetadataResponse response = blogService.getMetadata(blogId);

            assertThat(response.getAuthorId()).isEqualTo(authorId);
        }

        @Test
        @DisplayName("should return correct slug")
        void shouldReturnCorrectSlug() {
            Long blogId = 5L;
            BlogPost post = createBlogPost(blogId, "Test Blog", PostStatus.DRAFT);
            post.setSlug("test-blog");

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            BlogMetadataResponse response = blogService.getMetadata(blogId);

            assertThat(response.getSlug()).isEqualTo("test-blog");
        }

        @Test
        @DisplayName("should return creation timestamp")
        void shouldReturnCreationTimestamp() {
            Long blogId = 6L;
            Instant now = Instant.now();
            BlogPost post = createBlogPost(blogId, "Timestamped", PostStatus.DRAFT);
            post.setCreatedAt(now);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            BlogMetadataResponse response = blogService.getMetadata(blogId);

            assertThat(response.getCreatedAt()).isEqualTo(now);
        }
    }

    // ==================== GET CONTENT TESTS ====================
    @Nested
    @DisplayName("GetContent Tests")
    class GetContentTests {

        @Test
        @DisplayName("should return content when blog exists")
        void shouldReturnContent_whenBlogExists() {
            Long blogId = 1L;
            BlogPostContent content = createBlogContent(blogId, "# Hello Markdown", "MARKDOWN");

            when(contentRepository.findByPostId(blogId)).thenReturn(Optional.of(content));

            BlogContentResponse response = blogService.getContent(blogId);

            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(blogId);
            assertThat(response.getContent()).contains("Hello");
            assertThat(response.getFormat()).isEqualTo("MARKDOWN");
            verify(contentRepository).findByPostId(blogId);
        }

        @Test
        @DisplayName("should throw exception when blog id is null")
        void shouldThrowException_whenBlogIdIsNull() {
            assertThatThrownBy(() -> blogService.getContent(null))
                    .isInstanceOf(BlogNotFoundException.class)
                    .hasMessageContaining("Blog not found");
        }

        @Test
        @DisplayName("should throw exception when blog id is invalid")
        void shouldThrowException_whenBlogIdIsInvalid() {
            Long blogId = 999L;
            when(contentRepository.findByPostId(blogId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> blogService.getContent(blogId))
                    .isInstanceOf(BlogNotFoundException.class)
                    .hasMessageContaining("Blog not found");

            verify(contentRepository).findByPostId(blogId);
        }

        @Test
        @DisplayName("should handle long content correctly")
        void shouldHandleLongContentCorrectly() {
            Long blogId = 2L;
            String longContent = "# Long Article\n\n" + "This is a paragraph.\n".repeat(100);
            BlogPostContent content = createBlogContent(blogId, longContent, "MARKDOWN");

            when(contentRepository.findByPostId(blogId)).thenReturn(Optional.of(content));

            BlogContentResponse response = blogService.getContent(blogId);

            assertThat(response.getContent()).startsWith("# Long Article");
            assertThat(response.getContent()).contains("This is a paragraph.");
        }

        @Test
        @DisplayName("should preserve content formatting")
        void shouldPreserveContentFormatting() {
            Long blogId = 3L;
            String formattedContent = "# Heading\n\n**Bold** *Italic* `Code`";
            BlogPostContent content = createBlogContent(blogId, formattedContent, "MARKDOWN");

            when(contentRepository.findByPostId(blogId)).thenReturn(Optional.of(content));

            BlogContentResponse response = blogService.getContent(blogId);

            assertThat(response.getContent()).isEqualTo(formattedContent);
        }

        @Test
        @DisplayName("should return correct post id")
        void shouldReturnCorrectPostId() {
            Long blogId = 4L;
            BlogPostContent content = createBlogContent(blogId, "Content", "MARKDOWN");

            when(contentRepository.findByPostId(blogId)).thenReturn(Optional.of(content));

            BlogContentResponse response = blogService.getContent(blogId);

            assertThat(response.getPostId()).isEqualTo(blogId);
        }

        @Test
        @DisplayName("should return correct format type")
        void shouldReturnCorrectFormatType() {
            Long blogId = 5L;
            BlogPostContent content = createBlogContent(blogId, "Content", "MARKDOWN");

            when(contentRepository.findByPostId(blogId)).thenReturn(Optional.of(content));

            BlogContentResponse response = blogService.getContent(blogId);

            assertThat(response.getFormat()).isEqualTo("MARKDOWN");
        }

        @Test
        @DisplayName("should handle special characters in content")
        void shouldHandleSpecialCharactersInContent() {
            Long blogId = 6L;
            String contentWithSpecialChars = "Content with <tags>, {braces}, and @symbols";
            BlogPostContent content = createBlogContent(blogId, contentWithSpecialChars, "MARKDOWN");

            when(contentRepository.findByPostId(blogId)).thenReturn(Optional.of(content));

            BlogContentResponse response = blogService.getContent(blogId);

            assertThat(response.getContent()).isEqualTo(contentWithSpecialChars);
        }
    }

    // ==================== UPDATE BLOG TESTS ====================
    @Nested
    @DisplayName("UpdateBlog Tests")
    class UpdateBlogTests {

        @Test
        @DisplayName("should update blog with valid request")
        void shouldUpdateBlog_withValidRequest() {
            Long blogId = 1L;
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("Updated Title");
            request.setMarkdown("Updated content");

            BlogPost existingPost = createBlogPost(blogId, "Old Title", PostStatus.DRAFT);
            BlogPostContent existingContent = createBlogContent(blogId, "Old", "MARKDOWN");

            when(postRepository.findById(blogId)).thenReturn(Optional.of(existingPost));
            when(contentRepository.findById(blogId)).thenReturn(Optional.of(existingContent));
            when(versionRepository.findByPostIdOrderByVersionDesc(blogId))
                    .thenReturn(Collections.singletonList(createBlogVersion(blogId, 1)));
            when(versionRepository.save(any(BlogPostVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            blogService.updateBlog(blogId, request, AUTHOR_UUID);

            assertThat(existingPost.getTitle()).isEqualTo("Updated Title");
            assertThat(existingContent.getContent()).isEqualTo("Updated content");
        }

        @Test
        @DisplayName("should deny an update by a different user")
        void shouldDenyUpdateByAnotherUser() {
            Long blogId = 10L;
            BlogPost post = createBlogPost(blogId, "Owned by someone else", PostStatus.DRAFT);
            post.setAuthorUuid(UUID.randomUUID());
            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            assertThatThrownBy(() -> blogService.updateBlog(blogId, new UpdateBlogRequest("Changed", "Changed"),
                    AUTHOR_UUID)).isInstanceOf(AccessDeniedException.class);
            verify(contentRepository, never()).findById(blogId);
        }

        @Test
        @DisplayName("should deny updates to legacy posts without an owner")
        void shouldDenyUpdateToLegacyAnonymousPost() {
            Long blogId = 11L;
            BlogPost post = createBlogPost(blogId, "Legacy post", PostStatus.DRAFT);
            post.setAuthorUuid(null);
            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            assertThatThrownBy(() -> blogService.updateBlog(blogId, new UpdateBlogRequest("Changed", "Changed"),
                    AUTHOR_UUID)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("should throw exception when updating non-existent blog")
        void shouldThrowException_whenUpdatingNonExistentBlog() {
            Long blogId = 999L;
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("Title");
            request.setMarkdown("Content");

            when(postRepository.findById(blogId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> blogService.updateBlog(blogId, request, AUTHOR_UUID))
                    .isInstanceOf(BlogNotFoundException.class);
        }

        @Test
        @DisplayName("should update markdown content")
        void shouldUpdateMarkdownContent() {
            Long blogId = 2L;
            String newMarkdown = "# New Content\nWith more text";
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("Blog");
            request.setMarkdown(newMarkdown);

            BlogPost existingPost = createBlogPost(blogId, "Blog", PostStatus.DRAFT);
            BlogPostContent content = createBlogContent(blogId, "Old", "MARKDOWN");

            when(postRepository.findById(blogId)).thenReturn(Optional.of(existingPost));
            when(contentRepository.findById(blogId)).thenReturn(Optional.of(content));
            when(versionRepository.findByPostIdOrderByVersionDesc(blogId))
                    .thenReturn(Collections.singletonList(createBlogVersion(blogId, 1)));

            blogService.updateBlog(blogId, request, AUTHOR_UUID);

            ArgumentCaptor<BlogPostVersion> versionCaptor = ArgumentCaptor.forClass(BlogPostVersion.class);
            verify(versionRepository).save(versionCaptor.capture());
            assertThat(versionCaptor.getValue().getContent()).isEqualTo(newMarkdown);
        }

        @Test
        @DisplayName("should regenerate slug from new title")
        void shouldRegenerateSlugFromNewTitle() {
            Long blogId = 3L;
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("New Blog Title");
            request.setMarkdown("Content");

            BlogPost existingPost = createBlogPost(blogId, "Old", PostStatus.DRAFT);
            BlogPostContent existingContent = createBlogContent(blogId, "Old", "MARKDOWN");

            when(postRepository.findById(blogId)).thenReturn(Optional.of(existingPost));
            when(contentRepository.findById(blogId)).thenReturn(Optional.of(existingContent));
            when(versionRepository.findByPostIdOrderByVersionDesc(blogId))
                    .thenReturn(Collections.singletonList(createBlogVersion(blogId, 1)));
            when(versionRepository.save(any(BlogPostVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            blogService.updateBlog(blogId, request, AUTHOR_UUID);

            assertThat(existingPost.getSlug()).isNotEmpty();
            verify(versionRepository).save(any(BlogPostVersion.class));
        }

        @Test
        @DisplayName("should increment version number")
        void shouldIncrementVersionNumber() {
            Long blogId = 4L;
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("Blog");
            request.setMarkdown("Content");

            BlogPost existingPost = createBlogPost(blogId, "Blog", PostStatus.DRAFT);
            BlogPostVersion v1 = createBlogVersion(blogId, 1);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(existingPost));
            when(contentRepository.findById(blogId)).thenReturn(Optional.of(createBlogContent(blogId, "Old", "MARKDOWN")));
            when(versionRepository.findByPostIdOrderByVersionDesc(blogId))
                    .thenReturn(Collections.singletonList(v1));

            blogService.updateBlog(blogId, request, AUTHOR_UUID);

            ArgumentCaptor<BlogPostVersion> versionCaptor = ArgumentCaptor.forClass(BlogPostVersion.class);
            verify(versionRepository).save(versionCaptor.capture());
            assertThat(versionCaptor.getValue().getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("should preserve blog status when updating")
        void shouldPreserveStatusWhenUpdating() {
            Long blogId = 5L;
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("Updated");
            request.setMarkdown("Content");

            BlogPost existingPost = createBlogPost(blogId, "Blog", PostStatus.PUBLISHED);
            BlogPostContent existingContent = createBlogContent(blogId, "Old", "MARKDOWN");

            when(postRepository.findById(blogId)).thenReturn(Optional.of(existingPost));
            when(contentRepository.findById(blogId)).thenReturn(Optional.of(existingContent));
            when(versionRepository.findByPostIdOrderByVersionDesc(blogId))
                    .thenReturn(Collections.singletonList(createBlogVersion(blogId, 1)));
            when(versionRepository.save(any(BlogPostVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            blogService.updateBlog(blogId, request, AUTHOR_UUID);

            assertThat(existingPost.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        }

        @Test
        @DisplayName("should set updated timestamp")
        void shouldSetUpdatedTimestamp() {
            Long blogId = 6L;
            UpdateBlogRequest request = new UpdateBlogRequest();
            request.setTitle("Updated Blog");
            request.setMarkdown("Updated");

            BlogPost existingPost = createBlogPost(blogId, "Blog", PostStatus.DRAFT);
            BlogPostContent existingContent = createBlogContent(blogId, "Old", "MARKDOWN");

            when(postRepository.findById(blogId)).thenReturn(Optional.of(existingPost));
            when(contentRepository.findById(blogId)).thenReturn(Optional.of(existingContent));
            when(versionRepository.findByPostIdOrderByVersionDesc(blogId))
                    .thenReturn(Collections.singletonList(createBlogVersion(blogId, 1)));
            when(versionRepository.save(any(BlogPostVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            blogService.updateBlog(blogId, request, AUTHOR_UUID);

            assertThat(existingPost.getUpdatedAt()).isNotNull();
        }
    }

    // ==================== DELETE BLOG TESTS ====================
    @Nested
    @DisplayName("DeleteBlog Tests")
    class DeleteBlogTests {

        @Test
        @DisplayName("should delete blog successfully")
        void shouldDeleteBlogSuccessfully() {
            Long blogId = 1L;
            when(postRepository.findById(blogId))
                    .thenReturn(Optional.of(createBlogPost(blogId, "Blog", PostStatus.DRAFT)));

            blogService.deleteBlog(blogId, AUTHOR_UUID);

            verify(postRepository).delete(any(BlogPost.class));
        }

        @Test
        @DisplayName("should deny deletion by a different user")
        void shouldDenyDeleteByAnotherUser() {
            Long blogId = 12L;
            BlogPost post = createBlogPost(blogId, "Someone else's post", PostStatus.DRAFT);
            post.setAuthorUuid(UUID.randomUUID());
            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            assertThatThrownBy(() -> blogService.deleteBlog(blogId, AUTHOR_UUID))
                    .isInstanceOf(AccessDeniedException.class);
            verify(postRepository, never()).delete(any(BlogPost.class));
        }

        @Test
        @DisplayName("should delete draft blog")
        void shouldDeleteDraftBlog() {
            Long blogId = 2L;
            when(postRepository.findById(blogId))
                    .thenReturn(Optional.of(createBlogPost(blogId, "Draft", PostStatus.DRAFT)));

            blogService.deleteBlog(blogId, AUTHOR_UUID);

            verify(postRepository).delete(any(BlogPost.class));
        }

        @Test
        @DisplayName("should delete published blog")
        void shouldDeletePublishedBlog() {
            Long blogId = 3L;
            when(postRepository.findById(blogId))
                    .thenReturn(Optional.of(createBlogPost(blogId, "Published", PostStatus.PUBLISHED)));

            blogService.deleteBlog(blogId, AUTHOR_UUID);

            verify(postRepository).delete(any(BlogPost.class));
        }

        @Test
        @DisplayName("should cascade delete related content")
        void shouldCascadeDeleteRelatedContent() {
            Long blogId = 4L;
            when(postRepository.findById(blogId))
                    .thenReturn(Optional.of(createBlogPost(blogId, "Blog", PostStatus.DRAFT)));

            blogService.deleteBlog(blogId, AUTHOR_UUID);

            verify(postRepository).delete(any(BlogPost.class));
        }

        @Test
        @DisplayName("should return not found when deleting a missing blog")
        void shouldReturnNotFoundWhenDeletingMissingBlog() {
            Long blogId = 5L;

            when(postRepository.findById(blogId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> blogService.deleteBlog(blogId, AUTHOR_UUID))
                    .isInstanceOf(BlogNotFoundException.class);
        }
    }

    // ==================== GET ALL BLOGS TESTS ====================
    @Nested
    @DisplayName("GetAllBlogs Tests")
    class GetAllBlogsTests {

        @Test
        @DisplayName("should return all blogs with pagination")
        void shouldReturnAllBlogs_withPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            List<BlogPost> posts = List.of(
                    createBlogPost(1L, "First Blog", PostStatus.PUBLISHED),
                    createBlogPost(2L, "Second Blog", PostStatus.DRAFT),
                    createBlogPost(3L, "Third Blog", PostStatus.PUBLISHED)
            );
            Page<BlogPost> page = new PageImpl<>(posts, pageable, 3);

            when(postRepository.findAll(pageable)).thenReturn(page);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(3);
            verify(postRepository).findAll(pageable);
        }

        @Test
        @DisplayName("should return empty page when no blogs exist")
        void shouldReturnEmptyPage_whenNoBlogsExist() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<BlogPost> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(postRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("should map blog posts to metadata responses")
        void shouldMapBlogPostsToMetadataResponses() {
            Pageable pageable = PageRequest.of(0, 5);
            BlogPost post = createBlogPost(1L, "Test Blog", PostStatus.DRAFT);
            post.setAuthorId(123L);
            post.setSlug("test-blog");
            List<BlogPost> posts = List.of(post);
            Page<BlogPost> page = new PageImpl<>(posts, pageable, 1);

            when(postRepository.findAll(pageable)).thenReturn(page);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            assertThat(result.getContent()).hasSize(1);
            BlogMetadataResponse response = result.getContent().get(0);
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Test Blog");
            assertThat(response.getStatus()).isEqualTo("DRAFT");
            assertThat(response.getAuthorId()).isEqualTo(123L);
            assertThat(response.getSlug()).isEqualTo("test-blog");
        }

        @Test
        @DisplayName("should respect pagination parameters")
        void shouldRespectPaginationParameters() {
            Pageable pageable = PageRequest.of(1, 5);
            List<BlogPost> posts = List.of(
                    createBlogPost(6L, "Sixth Blog", PostStatus.DRAFT),
                    createBlogPost(7L, "Seventh Blog", PostStatus.PUBLISHED)
            );
            Page<BlogPost> page = new PageImpl<>(posts, pageable, 12);

            when(postRepository.findAll(pageable)).thenReturn(page);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            assertThat(result.getTotalElements()).isEqualTo(12);
            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("should include published timestamp for published blogs")
        void shouldIncludePublishedTimestampForPublishedBlogs() {
            Pageable pageable = PageRequest.of(0, 10);
            BlogPost publishedPost = createBlogPost(1L, "Published", PostStatus.PUBLISHED);
            publishedPost.setPublishedAt(Instant.now());
            List<BlogPost> posts = List.of(publishedPost);
            Page<BlogPost> page = new PageImpl<>(posts, pageable, 1);

            when(postRepository.findAll(pageable)).thenReturn(page);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            BlogMetadataResponse response = result.getContent().get(0);
            assertThat(response.getPublishedAt()).isNotNull();
        }

        @Test
        @DisplayName("should not include published timestamp for draft blogs")
        void shouldNotIncludePublishedTimestampForDraftBlogs() {
            Pageable pageable = PageRequest.of(0, 10);
            BlogPost draftPost = createBlogPost(1L, "Draft", PostStatus.DRAFT);
            List<BlogPost> posts = List.of(draftPost);
            Page<BlogPost> page = new PageImpl<>(posts, pageable, 1);

            when(postRepository.findAll(pageable)).thenReturn(page);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            BlogMetadataResponse response = result.getContent().get(0);
            assertThat(response.getPublishedAt()).isNull();
        }

        @Test
        @DisplayName("should return multiple pages of blogs")
        void shouldReturnMultiplePagesOfBlogs() {
            Pageable pageable = PageRequest.of(0, 2);
            List<BlogPost> firstPagePosts = List.of(
                    createBlogPost(1L, "First", PostStatus.DRAFT),
                    createBlogPost(2L, "Second", PostStatus.PUBLISHED)
            );
            Page<BlogPost> firstPage = new PageImpl<>(firstPagePosts, pageable, 5);

            when(postRepository.findAll(pageable)).thenReturn(firstPage);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("should include creation timestamp for all blogs")
        void shouldIncludeCreationTimestampForAllBlogs() {
            Pageable pageable = PageRequest.of(0, 10);
            BlogPost post = createBlogPost(1L, "Test", PostStatus.DRAFT);
            List<BlogPost> posts = List.of(post);
            Page<BlogPost> page = new PageImpl<>(posts, pageable, 1);

            when(postRepository.findAll(pageable)).thenReturn(page);

            Page<BlogMetadataResponse> result = blogService.getAllBlogs(pageable);

            BlogMetadataResponse response = result.getContent().get(0);
            assertThat(response.getCreatedAt()).isNotNull();
        }
    }

    // ==================== PUBLISH BLOG TESTS ====================
    @Nested
    @DisplayName("PublishBlog Tests")
    class PublishBlogTests {

        @Test
        @DisplayName("should publish draft blog")
        void shouldPublishDraftBlog() {
            Long blogId = 1L;
            BlogPost post = createBlogPost(blogId, "Draft Blog", PostStatus.DRAFT);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            blogService.publish(blogId, AUTHOR_UUID);

            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        }

        @Test
        @DisplayName("should deny publishing by a different user")
        void shouldDenyPublishByAnotherUser() {
            Long blogId = 13L;
            BlogPost post = createBlogPost(blogId, "Someone else's post", PostStatus.DRAFT);
            post.setAuthorUuid(UUID.randomUUID());
            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            assertThatThrownBy(() -> blogService.publish(blogId, AUTHOR_UUID))
                    .isInstanceOf(AccessDeniedException.class);
            assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
        }

        @Test
        @DisplayName("should throw exception when publishing non-existent blog")
        void shouldThrowException_whenPublishingNonExistentBlog() {
            Long blogId = 999L;

            when(postRepository.findById(blogId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> blogService.publish(blogId, AUTHOR_UUID))
                    .isInstanceOf(BlogNotFoundException.class);
        }

        @Test
        @DisplayName("should set published status")
        void shouldSetPublishedStatus() {
            Long blogId = 2L;
            BlogPost post = createBlogPost(blogId, "Article", PostStatus.DRAFT);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            blogService.publish(blogId, AUTHOR_UUID);

            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        }

        @Test
        @DisplayName("should set published timestamp")
        void shouldSetPublishedTimestamp() {
            Long blogId = 3L;
            BlogPost post = createBlogPost(blogId, "Article", PostStatus.DRAFT);

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            blogService.publish(blogId, AUTHOR_UUID);

            assertThat(post.getPublishedAt()).isNotNull();
        }

        @Test
        @DisplayName("should allow re-publishing already published blog")
        void shouldAllowRePublishingAlreadyPublishedBlog() {
            Long blogId = 4L;
            BlogPost post = createBlogPost(blogId, "Published", PostStatus.PUBLISHED);
            post.setPublishedAt(Instant.now().minusSeconds(3600));

            when(postRepository.findById(blogId)).thenReturn(Optional.of(post));

            assertThatNoException().isThrownBy(() -> blogService.publish(blogId, AUTHOR_UUID));
        }
    }

    // ==================== HELPER METHODS ====================
    private BlogPost createBlogPost(Long id, String title, PostStatus status) {
        BlogPost post = new BlogPost();
        post.setId(id);
        post.setTitle(title);
        post.setSlug(title.toLowerCase().replace(" ", "-"));
        post.setAuthorId(100L);
        post.setAuthorUuid(AUTHOR_UUID);
        post.setStatus(status);
        post.setCreatedAt(Instant.now());
        if (status == PostStatus.PUBLISHED) {
            post.setPublishedAt(Instant.now());
        }
        return post;
    }

    private BlogPostContent createBlogContent(Long postId, String content, String format) {
        BlogPostContent blogContent = new BlogPostContent();
        blogContent.setPostId(postId);
        blogContent.setContent(content);
        blogContent.setFormat(format);
        return blogContent;
    }

    private BlogPostVersion createBlogVersion(Long postId, int version) {
        BlogPostVersion v = new BlogPostVersion();
        v.setPostId(postId);
        v.setVersion(version);
        v.setContent("Content v" + version);
        v.setCreatedAt(Instant.now());
        return v;
    }
}
