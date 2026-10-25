package com.devopswithprashant.service.blog.infrastructure.client;

import com.github.benmanes.caffeine.cache.Cache;
import com.devopswithprashant.service.blog.api.dto.AuthorProfileResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class ProfileServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceClient.class);

    private final RestClient restClient;
    private final Cache<UUID, Optional<AuthorProfileResponse>> publicProfileCache;

    public ProfileServiceClient(
            RestClient profileServiceRestClient,
            Cache<UUID, Optional<AuthorProfileResponse>> publicProfileCache) {
        this.restClient = profileServiceRestClient;
        this.publicProfileCache = publicProfileCache;
    }

    public Optional<AuthorProfileResponse> getPublicProfile(UUID profileId) {
        Objects.requireNonNull(profileId, "profileId is required");
        Optional<AuthorProfileResponse> cached = publicProfileCache.getIfPresent(profileId);
        if (cached != null) {
            return cached;
        }

        ProfileLookupResult result = fetchPublicProfile(profileId);
        if (result.cacheable()) {
            publicProfileCache.put(profileId, result.profile());
        }
        return result.profile();
    }

    private ProfileLookupResult fetchPublicProfile(UUID profileId) {
        try {
            AuthorProfileResponse profile = restClient.get()
                    .uri("/api/v1/profiles/{profileId}", profileId)
                    .retrieve()
                    .body(AuthorProfileResponse.class);
            if (profile == null || !profileId.equals(profile.userId())) {
                log.error("Profile Service returned no profile or a mismatched profile id requestedProfileId={}",
                        profileId);
                return new ProfileLookupResult(Optional.empty(), false);
            }
            return new ProfileLookupResult(Optional.of(profile), true);
        } catch (HttpClientErrorException.NotFound exception) {
            log.debug("No public profile found requestedProfileId={}", profileId);
            return new ProfileLookupResult(Optional.empty(), true);
        } catch (HttpServerErrorException | ResourceAccessException exception) {
            log.warn("Profile Service unavailable; author details omitted requestedProfileId={} cause={}",
                    profileId, exception.getClass().getSimpleName());
            return new ProfileLookupResult(Optional.empty(), false);
        }
    }

    private record ProfileLookupResult(Optional<AuthorProfileResponse> profile, boolean cacheable) {
    }
}
