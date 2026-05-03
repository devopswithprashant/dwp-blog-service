package com.devopswithprashant.service.blog.infrastructure.repository;


import com.devopswithprashant.service.blog.domain.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPost> findBySlug(String slug);
}
