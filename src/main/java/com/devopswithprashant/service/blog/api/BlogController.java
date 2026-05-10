package com.devopswithprashant.service.blog.api;

import com.devopswithprashant.service.blog.api.dto.*;
import com.devopswithprashant.service.blog.application.BlogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private static final Logger log = LoggerFactory.getLogger(BlogController.class);

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    /* -------- CREATE -------- */

    @PostMapping
    public ResponseEntity<Long> createDraft(
            @Valid @RequestBody CreateBlogRequest request) {

        log.info("Create draft blog requested authorId={} titleLength={}",
                request.getAuthorId(),
                request.getTitle() == null ? 0 : request.getTitle().length()
        );
        Long postId = blogService.createDraft(
                request.getAuthorId(),
                request.getTitle(),
                request.getMarkdown()
        );
        log.info("Create draft blog succeeded postId={}", postId);
        return ResponseEntity.ok(postId);
    }

    /* -------- READ -------- */

    @GetMapping("/{id}/metadata")
    public ResponseEntity<BlogMetadataResponse> getMetadata(@PathVariable Long id) {
        log.debug("Get blog metadata requested postId={}", id);
        return ResponseEntity.ok(blogService.getMetadata(id));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<BlogContentResponse> getContent(@PathVariable Long id) {
        log.debug("Get blog content requested postId={}", id);
        return ResponseEntity.ok(blogService.getContent(id));
    }

    @GetMapping
    public ResponseEntity<Page<BlogMetadataResponse>> getAllBlogs(Pageable pageable) {
        log.debug("List blogs requested page={} size={} sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
        return ResponseEntity.ok(blogService.getAllBlogs(pageable));
    }

    /* -------- UPDATE -------- */

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBlogRequest request) {

        log.info("Update blog requested postId={} titleLength={} markdownLength={}",
                id,
                request.getTitle() == null ? 0 : request.getTitle().length(),
                request.getMarkdown() == null ? 0 : request.getMarkdown().length()
        );
        blogService.updateBlog(id, request);
        log.info("Update blog succeeded postId={}", id);
        return ResponseEntity.ok().build();
    }

    /* -------- DELETE -------- */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        log.info("Delete blog requested postId={}", id);
        blogService.deleteBlog(id);
        log.info("Delete blog succeeded postId={}", id);
        return ResponseEntity.noContent().build();
    }

    /* -------- PUBLISH -------- */

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable Long id) {
        log.info("Publish blog requested postId={}", id);
        blogService.publish(id);
        log.info("Publish blog succeeded postId={}", id);
        return ResponseEntity.ok().build();
    }
}
