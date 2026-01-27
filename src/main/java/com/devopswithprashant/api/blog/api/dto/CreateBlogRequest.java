package com.devopswithprashant.api.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBlogRequest {

    @NotNull
    private Long authorId;

    @NotBlank
    private String title;

    @NotBlank
    private String markdown;

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

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public CreateBlogRequest(@NotNull Long authorId, @NotBlank String title, @NotBlank String markdown) {
        this.authorId = authorId;
        this.title = title;
        this.markdown = markdown;
    }

    public CreateBlogRequest() {
        super();
    }

    @Override
    public String toString() {
        return "CreateBlogRequest [authorId=" + authorId + ", title=" + title + ", markdown=" + markdown + "]";
    }
    // getters & setters
}