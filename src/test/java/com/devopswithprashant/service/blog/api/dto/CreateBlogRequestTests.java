package com.devopswithprashant.service.blog.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CreateBlogRequestTests {

    @Test
    void carriesOnlyBlogContent() {
        CreateBlogRequest request = new CreateBlogRequest("Test Title", "# Test Content");

        assertThat(request.getTitle()).isEqualTo("Test Title");
        assertThat(request.getMarkdown()).isEqualTo("# Test Content");
    }

    @Test
    void supportsJsonBinding() {
        CreateBlogRequest request = new CreateBlogRequest();
        request.setTitle("Bound title");
        request.setMarkdown("Bound content");

        assertThat(request.getTitle()).isEqualTo("Bound title");
        assertThat(request.getMarkdown()).isEqualTo("Bound content");
    }
}
