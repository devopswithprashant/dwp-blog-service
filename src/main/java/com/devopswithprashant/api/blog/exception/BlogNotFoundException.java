package com.devopswithprashant.api.blog.exception;

public class BlogNotFoundException extends RuntimeException {

    public BlogNotFoundException(Long blogId) {
        super("Blog not found with id: " + blogId);
    }
}
