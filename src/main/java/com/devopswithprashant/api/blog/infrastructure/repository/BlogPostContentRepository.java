package com.devopswithprashant.api.blog.infrastructure.repository;

import com.devopswithprashant.api.blog.domain.BlogPostContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogPostContentRepository
        extends JpaRepository<BlogPostContent, Long> {
    
    Optional<BlogPostContent> findByPostId(Long postId);
}
