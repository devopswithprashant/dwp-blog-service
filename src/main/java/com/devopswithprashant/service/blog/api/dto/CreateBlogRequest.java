package com.devopswithprashant.service.blog.api.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBlogRequest {

    @NotBlank
    private String authorIdentity;

    @NotBlank
    private String title;

    @NotBlank
    private String markdown;

    public Long getAuthorId() {
        if (authorIdentity == null || authorIdentity.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(authorIdentity.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public String getAuthorIdentity() {
        return authorIdentity;
    }

    public void setAuthorId(Object authorId) {
        this.authorIdentity = authorId == null ? null : authorId.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public CreateBlogRequest(@NotBlank String authorIdentity, @NotBlank String title, @NotBlank String markdown) {
        this.authorIdentity = authorIdentity;
        this.title = title;
        this.markdown = markdown;
    }

    public CreateBlogRequest(Long authorId, @NotBlank String title, @NotBlank String markdown) {
        this(authorId == null ? null : authorId.toString(), title, markdown);
    }

    public CreateBlogRequest() {
        super();
    }

    @Override
    public String toString() {
        return "CreateBlogRequest [authorId=" + authorIdentity + ", title=" + title + ", markdown=" + markdown + "]";
    }
    // getters & setters
}