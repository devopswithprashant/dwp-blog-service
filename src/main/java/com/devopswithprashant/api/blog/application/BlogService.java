package com.devopswithprashant.api.blog.application;

import com.devopswithprashant.api.blog.api.dto.*;
import com.devopswithprashant.api.blog.domain.*;
import com.devopswithprashant.api.blog.infrastructure.repository.*;
import com.devopswithprashant.api.blog.exception.BlogNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@Transactional
public class BlogService {

    private final BlogPostRepository postRepo;
    private final BlogPostContentRepository contentRepo;
    private final BlogPostVersionRepository versionRepo;

    public BlogService(BlogPostRepository postRepo,
                       BlogPostContentRepository contentRepo,
                       BlogPostVersionRepository versionRepo) {
        this.postRepo = postRepo;
        this.contentRepo = contentRepo;
        this.versionRepo = versionRepo;
    }

    /* ---------------- CREATE ---------------- */

    public Long createDraft(Long authorId, String title, String markdown) {

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

        return post.getId();
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

        BlogPostContent content = contentRepo.findByPostId(blogId)
                .orElseThrow(() -> new BlogNotFoundException(blogId));

        BlogContentResponse resp = new BlogContentResponse();
        resp.setPostId(content.getPostId());
        resp.setContent(content.getContent());
        resp.setFormat(content.getFormat());

        return resp;
    }

    @Transactional(readOnly = true)
    public Page<BlogMetadataResponse> getAllBlogs(Pageable pageable) {
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
    }

    /* ---------------- UPDATE ---------------- */

    public void updateBlog(Long blogId, UpdateBlogRequest request) {

        BlogPost post = postRepo.findById(blogId).orElseThrow();
        post.setTitle(request.getTitle());
        post.setSlug(generateSlug(request.getTitle()));
        post.setUpdatedAt(Instant.now());

        BlogPostContent content = contentRepo.findById(blogId).orElseThrow();
        content.setContent(request.getMarkdown());

        saveVersion(blogId, request.getMarkdown());
    }

    /* ---------------- DELETE ---------------- */

    public void deleteBlog(Long blogId) {
        postRepo.deleteById(blogId); // CASCADE deletes content & versions
    }

    /* ---------------- PUBLISH ---------------- */

    public void publish(Long postId) {
        BlogPost post = postRepo.findById(postId).orElseThrow();
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(Instant.now());
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
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
