package com.devopswithprashant.service.blog.application;

import com.devopswithprashant.service.blog.api.dto.*;
import com.devopswithprashant.service.blog.domain.*;
import com.devopswithprashant.service.blog.infrastructure.repository.*;
import com.devopswithprashant.service.blog.infrastructure.metrics.BlogMetrics;
import com.devopswithprashant.service.blog.exception.BlogNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@Transactional
public class BlogService {

    private static final Logger log = LoggerFactory.getLogger(BlogService.class);

    private final BlogPostRepository postRepo;
    private final BlogPostContentRepository contentRepo;
    private final BlogPostVersionRepository versionRepo;
    private final BlogMetrics blogMetrics;

    public BlogService(BlogPostRepository postRepo,
                       BlogPostContentRepository contentRepo,
                       BlogPostVersionRepository versionRepo,
                       BlogMetrics blogMetrics) {
        this.postRepo = postRepo;
        this.contentRepo = contentRepo;
        this.versionRepo = versionRepo;
        this.blogMetrics = blogMetrics;
    }

    /* ---------------- CREATE ---------------- */

    public Long createDraft(Long authorId, String title, String markdown) {
        return blogMetrics.recordTimed("create_draft", () -> {
            log.debug("Creating draft blog authorId={} titleLength={} markdownLength={}",
                    authorId,
                    title == null ? 0 : title.length(),
                    markdown == null ? 0 : markdown.length()
            );
            BlogPost post = new BlogPost();
            post.setAuthorId(authorId);
            post.setTitle(title);
            post.setSlug(generateSlug(title));
            post.setStatus(PostStatus.DRAFT);
            post.setCreatedAt(Instant.now());

            postRepo.save(post);

            BlogPostContent content = new BlogPostContent();
            content.setPostId(post.getId());
            content.setContent(markdown);
            content.setFormat("MARKDOWN");

            contentRepo.save(content);
            saveVersion(post.getId(), markdown);

            blogMetrics.recordPostCreated();
            log.info("Draft blog created postId={} authorId={} slug={}", post.getId(), authorId, post.getSlug());
            return post.getId();
        });
    }

    /* ---------------- READ ---------------- */

    // @Transactional(readOnly = true)
    // public BlogMetadataResponse getMetadata(Long blogId) {
    //     BlogPost post = postRepo.findById(blogId).orElseThrow();

    //     BlogMetadataResponse resp = new BlogMetadataResponse();
    //     resp.setId(post.getId());
    //     resp.setAuthorId(post.getAuthorId());
    //     resp.setTitle(post.getTitle());
    //     resp.setSlug(post.getSlug());
    //     resp.setStatus(post.getStatus().name());
    //     resp.setCreatedAt(post.getCreatedAt());
    //     resp.setPublishedAt(post.getPublishedAt());

    //     return resp;
    // }

    @Transactional(readOnly = true)
    public BlogMetadataResponse getMetadata(Long blogId) {
        return blogMetrics.recordTimed("get_metadata", () -> {
            log.debug("Fetching blog metadata postId={}", blogId);
            BlogPost post = postRepo.findById(blogId)
                    .orElseThrow(() -> new BlogNotFoundException(blogId));

            BlogMetadataResponse resp = new BlogMetadataResponse();
            resp.setId(post.getId());
            resp.setAuthorId(post.getAuthorId());
            resp.setTitle(post.getTitle());
            resp.setSlug(post.getSlug());
            resp.setStatus(post.getStatus().name());
            resp.setCreatedAt(post.getCreatedAt());
            resp.setPublishedAt(post.getPublishedAt());

            return resp;
        });
    }

    // @Transactional(readOnly = true)
    // public BlogContentResponse getContent(Long blogId) {
    //     BlogPostContent content = contentRepo.findById(blogId).orElseThrow();

    //     BlogContentResponse resp = new BlogContentResponse();
    //     resp.setPostId(content.getPostId());
    //     resp.setContent(content.getContent());
    //     resp.setFormat(content.getFormat());

    //     return resp;
    // }

    @Transactional(readOnly = true)
    public BlogContentResponse getContent(Long blogId) {
        return blogMetrics.recordTimed("get_content", () -> {
            log.debug("Fetching blog content postId={}", blogId);
            BlogPostContent content = contentRepo.findByPostId(blogId)
                    .orElseThrow(() -> new BlogNotFoundException(blogId));

            BlogContentResponse resp = new BlogContentResponse();
            resp.setPostId(content.getPostId());
            resp.setContent(content.getContent());
            resp.setFormat(content.getFormat());

            return resp;
        });
    }

    @Transactional(readOnly = true)
    public Page<BlogMetadataResponse> getAllBlogs(Pageable pageable) {
        return blogMetrics.recordTimed("list_blogs", () -> {
            log.debug("Listing blogs page={} size={} sort={}",
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSort()
            );
            return postRepo.findAll(pageable)
                    .map(post -> {
                        BlogMetadataResponse resp = new BlogMetadataResponse();
                        resp.setId(post.getId());
                        resp.setAuthorId(post.getAuthorId());
                        resp.setTitle(post.getTitle());
                        resp.setSlug(post.getSlug());
                        resp.setStatus(post.getStatus().name());
                        resp.setCreatedAt(post.getCreatedAt());
                        resp.setPublishedAt(post.getPublishedAt());
                        return resp;
                    });
        });
    }

    /* ---------------- UPDATE ---------------- */

    public void updateBlog(Long blogId, UpdateBlogRequest request) {
        blogMetrics.recordTimed("update_blog", () -> {
            log.debug("Updating blog postId={} titleLength={} markdownLength={}",
                    blogId,
                    request.getTitle() == null ? 0 : request.getTitle().length(),
                    request.getMarkdown() == null ? 0 : request.getMarkdown().length()
            );
            BlogPost post = postRepo.findById(blogId).orElseThrow();
            post.setTitle(request.getTitle());
            post.setSlug(generateSlug(request.getTitle()));
            post.setUpdatedAt(Instant.now());

            BlogPostContent content = contentRepo.findById(blogId).orElseThrow();
            content.setContent(request.getMarkdown());

            saveVersion(blogId, request.getMarkdown());
            blogMetrics.recordPostUpdated();
            log.info("Blog updated postId={} slug={}", blogId, post.getSlug());
        });
    }

    /* ---------------- DELETE ---------------- */

    public void deleteBlog(Long blogId) {
        blogMetrics.recordTimed("delete_blog", () -> {
            log.info("Deleting blog postId={}", blogId);
            postRepo.deleteById(blogId); // CASCADE deletes content & versions
            blogMetrics.recordPostDeleted();
        });
    }

    /* ---------------- PUBLISH ---------------- */

    public void publish(Long postId) {
        blogMetrics.recordTimed("publish_blog", () -> {
            log.info("Publishing blog postId={}", postId);
            BlogPost post = postRepo.findById(postId).orElseThrow();
            post.setStatus(PostStatus.PUBLISHED);
            post.setPublishedAt(Instant.now());
            blogMetrics.recordPostPublished();
            log.info("Blog published postId={} publishedAt={}", postId, post.getPublishedAt());
        });
    }

    /* ---------------- HELPERS ---------------- */

    private void saveVersion(Long postId, String content) {
        int version = versionRepo.findByPostIdOrderByVersionDesc(postId)
                .stream()
                .findFirst()
                .map(v -> v.getVersion() + 1)
                .orElse(1);

        BlogPostVersion v = new BlogPostVersion();
        v.setPostId(postId);
        v.setVersion(version);
        v.setContent(content);
        v.setCreatedAt(Instant.now());

        versionRepo.save(v);
        blogMetrics.recordVersionSaved();
        log.debug("Saved blog version postId={} version={}", postId, version);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
