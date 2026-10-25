package com.devopswithprashant.service.blog.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.devopswithprashant.service.blog.api.dto.AuthorProfileResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class ProfileServiceClientTest {

    private static final UUID PROFILE_ID = UUID.fromString("8c0c0b42-ff2a-4d68-b90f-3b845ca8106a");

    private MockRestServiceServer server;
    private ProfileServiceClient profileServiceClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://profile.test");
        server = MockRestServiceServer.bindTo(builder).build();
        Cache<UUID, Optional<AuthorProfileResponse>> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(10))
                .build();
        profileServiceClient = new ProfileServiceClient(builder.build(), cache);
    }

    @Test
    void retrievesPublicProfileAndCachesIt() {
        String profileJson = """
                {
                  "userId": "%s",
                  "displayName": "Ada",
                  "bio": "Engineer",
                  "avatarUrl": "https://images.example.test/ada.png",
                  "createdAt": "2026-10-18T12:00:00Z"
                }
                """.formatted(PROFILE_ID);
        server.expect(requestTo("http://profile.test/api/v1/profiles/" + PROFILE_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(profileJson, MediaType.APPLICATION_JSON));

        Optional<AuthorProfileResponse> first = profileServiceClient.getPublicProfile(PROFILE_ID);
        Optional<AuthorProfileResponse> second = profileServiceClient.getPublicProfile(PROFILE_ID);

        assertThat(first).contains(new AuthorProfileResponse(
                PROFILE_ID, "Ada", "Engineer", "https://images.example.test/ada.png",
                java.time.Instant.parse("2026-10-18T12:00:00Z")));
        assertThat(second).isEqualTo(first);
        server.verify();
    }

    @Test
    void treatsMissingProfileAsUnavailableAndCachesTheNotFound() {
        server.expect(requestTo("http://profile.test/api/v1/profiles/" + PROFILE_ID))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(profileServiceClient.getPublicProfile(PROFILE_ID)).isEmpty();
        assertThat(profileServiceClient.getPublicProfile(PROFILE_ID)).isEmpty();
        server.verify();
    }

    @Test
    void degradesOnProfileServiceFailureWithoutCachingTheFailure() {
        server.expect(requestTo("http://profile.test/api/v1/profiles/" + PROFILE_ID))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        server.expect(requestTo("http://profile.test/api/v1/profiles/" + PROFILE_ID))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThat(profileServiceClient.getPublicProfile(PROFILE_ID)).isEmpty();
        assertThat(profileServiceClient.getPublicProfile(PROFILE_ID)).isEmpty();
        server.verify();
    }

    @Test
    void doesNotUseAProfileWhoseIdDoesNotMatchTheRequest() {
        String otherId = UUID.randomUUID().toString();
        String profileJson = """
                {"userId":"%s","displayName":"Other"}
                """.formatted(otherId);
        server.expect(requestTo("http://profile.test/api/v1/profiles/" + PROFILE_ID))
                .andRespond(withSuccess(profileJson, MediaType.APPLICATION_JSON));

        assertThat(profileServiceClient.getPublicProfile(PROFILE_ID)).isEmpty();
        server.verify();
    }
}
