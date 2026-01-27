package com.devopswithprashant.api.blog.api.dto;


public class BlogContentResponse {

    private Long postId;
    private String content; // RAW MARKDOWN
    private String format;  // MARKDOWN
    public Long getPostId() {
        return postId;
    }
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    public BlogContentResponse(Long postId, String content, String format) {
        this.postId = postId;
        this.content = content;
        this.format = format;
    }

    public BlogContentResponse() {
        super();
    }

    // getters & setters
}
