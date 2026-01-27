package com.devopswithprashant.api.blog.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "blog_post_content")
public class BlogPostContent {

    @Id
    private Long postId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content; // RAW MARKDOWN

    private String format; // MARKDOWN

    public Long getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public String getFormat() {
        return format;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public BlogPostContent(Long postId, String content, String format) {
        this.postId = postId;
        this.content = content;
        this.format = format;
    }
    public BlogPostContent() {
        super();
    }
    
    @Override
    public String toString() {
        return "BlogPostContent [postId=" + postId + ", content=" + content + ", format=" + format + "]";
    }

    // getters & setters
}
