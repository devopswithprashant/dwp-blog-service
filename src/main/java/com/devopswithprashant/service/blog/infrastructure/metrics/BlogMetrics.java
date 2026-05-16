package com.devopswithprashant.service.blog.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class BlogMetrics {

    private final MeterRegistry registry;
    private final Counter postsCreated;
    private final Counter postsPublished;
    private final Counter postsDeleted;
    private final Counter postsUpdated;
    private final Counter versionsSaved;
    private final Counter postsNotFound;

    public BlogMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.postsCreated = Counter.builder("blog.posts.created")
                .description("Draft blog posts created")
                .register(registry);
        this.postsPublished = Counter.builder("blog.posts.published")
                .description("Blog posts published")
                .register(registry);
        this.postsDeleted = Counter.builder("blog.posts.deleted")
                .description("Blog posts deleted")
                .register(registry);
        this.postsUpdated = Counter.builder("blog.posts.updated")
                .description("Blog posts updated")
                .register(registry);
        this.versionsSaved = Counter.builder("blog.versions.saved")
                .description("Blog content versions saved")
                .register(registry);
        this.postsNotFound = Counter.builder("blog.posts.not_found")
                .description("Blog lookup failures (404)")
                .register(registry);
    }

    public void recordPostCreated() {
        postsCreated.increment();
    }

    public void recordPostPublished() {
        postsPublished.increment();
    }

    public void recordPostDeleted() {
        postsDeleted.increment();
    }

    public void recordPostUpdated() {
        postsUpdated.increment();
    }

    public void recordVersionSaved() {
        versionsSaved.increment();
    }

    public void recordPostNotFound() {
        postsNotFound.increment();
    }

    public void recordTimed(String operation, Runnable action) {
        registry.timer("blog.operation.duration", "operation", operation)
                .record(action);
    }

    public <T> T recordTimed(String operation, java.util.function.Supplier<T> action) {
        return registry.timer("blog.operation.duration", "operation", operation)
                .record(action);
    }
}
