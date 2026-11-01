package com.devopswithprashant.service.blog.infrastructure.repository;

import com.devopswithprashant.service.blog.domain.BlogPostContent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class BlogPostContentRepositoryIT {

    @Autowired
    private BlogPostContentRepository contentRepository;

    @Test
    void shouldSaveAndFetchBlogContent() {
        BlogPostContent content = new BlogPostContent();
        content.setPostId(1L);
        content.setContent("## Real DB Test");
        content.setFormat("MARKDOWN");

        contentRepository.save(content);

        Optional<BlogPostContent> found =
                contentRepository.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getContent())
                .contains("Real DB");
    }
}
