package com.devopswithprashant.api.blog.api;

import com.devopswithprashant.api.blog.api.dto.*;
import com.devopswithprashant.api.blog.application.BlogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    /* -------- CREATE -------- */

    @PostMapping
    public ResponseEntity<Long> createDraft(
            @Valid @RequestBody CreateBlogRequest request) {

        Long postId = blogService.createDraft(
                request.getAuthorId(),
                request.getTitle(),
                request.getMarkdown()
        );
        return ResponseEntity.ok(postId);
    }

    /* -------- READ -------- */

    @GetMapping("/{id}/metadata")
    public ResponseEntity<BlogMetadataResponse> getMetadata(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getMetadata(id));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<BlogContentResponse> getContent(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getContent(id));
    }

    @GetMapping
    public ResponseEntity<Page<BlogMetadataResponse>> getAllBlogs(Pageable pageable) {
        return ResponseEntity.ok(blogService.getAllBlogs(pageable));
    }

    /* -------- UPDATE -------- */

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBlogRequest request) {

        blogService.updateBlog(id, request);
        return ResponseEntity.ok().build();
    }

    /* -------- DELETE -------- */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    /* -------- PUBLISH -------- */

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable Long id) {
        blogService.publish(id);
        return ResponseEntity.ok().build();
    }
}
