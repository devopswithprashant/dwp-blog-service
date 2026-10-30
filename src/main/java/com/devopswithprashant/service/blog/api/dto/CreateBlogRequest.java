package com.devopswithprashant.service.blog.api.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBlogRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String markdown;

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

    public CreateBlogRequest(@NotBlank String title, @NotBlank String markdown) {
        this.title = title;
        this.markdown = markdown;
    }

    public CreateBlogRequest() {
        super();
    }

    @Override
    public String toString() {
        return "CreateBlogRequest [title=" + title + ", markdown=" + markdown + "]";
    }
}
