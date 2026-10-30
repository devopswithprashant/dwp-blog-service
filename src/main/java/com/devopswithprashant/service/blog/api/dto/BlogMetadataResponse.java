package com.devopswithprashant.service.blog.api.dto;

import java.time.Instant;
import java.util.UUID;

public class BlogMetadataResponse {

    private Long id;
    private Long authorId;
    private UUID authorUuid;
    private AuthorProfileResponse authorProfile;
    private String title;
    private String slug;
    private String status;
    private Instant createdAt;
    private Instant publishedAt;
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
    public UUID getAuthorUuid() {
        return authorUuid;
    }
    public void setAuthorUuid(UUID authorUuid) {
        this.authorUuid = authorUuid;
    }
    public AuthorProfileResponse getAuthorProfile() {
        return authorProfile;
    }
    public void setAuthorProfile(AuthorProfileResponse authorProfile) {
        this.authorProfile = authorProfile;
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
    public BlogMetadataResponse(Long id, Long authorId, String title, String slug, String status, Instant createdAt,
            Instant publishedAt) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.slug = slug;
        this.status = status;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
    }

    public BlogMetadataResponse() {
        super();
    }

    // getters & setters
}
