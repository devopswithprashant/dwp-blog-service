package com.devopswithprashant.service.blog.domain;


import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "blog_post_version")
public class BlogPostVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Integer version;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public BlogPostVersion(Long id, Long postId, Integer version, String content, Instant createdAt) {
        this.id = id;
        this.postId = postId;
        this.version = version;
        this.content = content;
        this.createdAt = createdAt;
    }
    public BlogPostVersion() {
        super();
    }

    @Override
    public String toString() {
        return "BlogPostVersion [id=" + id + ", postId=" + postId + ", version=" + version + ", content=" + content + ", createdAt=" + createdAt + "]";
    }

    // getters & setters
}
