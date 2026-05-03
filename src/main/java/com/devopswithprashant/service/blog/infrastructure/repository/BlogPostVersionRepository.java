package com.devopswithprashant.service.blog.infrastructure.repository;

import com.devopswithprashant.service.blog.domain.BlogPostVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogPostVersionRepository
        extends JpaRepository<BlogPostVersion, Long> {

    List<BlogPostVersion> findByPostIdOrderByVersionDesc(Long postId);
}
