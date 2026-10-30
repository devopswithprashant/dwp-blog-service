package com.devopswithprashant.service.blog.api.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthorProfileResponse(
        UUID userId,
        String displayName,
        String bio,
        String avatarUrl,
        Instant createdAt) {
}
