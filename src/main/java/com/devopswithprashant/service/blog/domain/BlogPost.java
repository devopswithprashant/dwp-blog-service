package com.devopswithprashant.service.blog.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "blog_post")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;

    private String title;

    @Column(unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;

    public BlogPost(Long id, Long authorId, String title, String slug, PostStatus status, Instant createdAt,
            Instant updatedAt, Instant publishedAt) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.slug = slug;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
    }

    public BlogPost() {
        super();
    }

    @Override
    public String toString() {
        return "BlogPost [id=" + id + ", authorId=" + authorId + ", title=" + title + ", slug=" + slug + ", status="
                + status + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", publishedAt=" + publishedAt
                + "]";
    }

    // getters & setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getAuthorId() {
        return authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public PostStatus getStatus() {
        return status;
    }
    public void setStatus(PostStatus status) {
        this.status = status;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Instant getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

}
