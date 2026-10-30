package com.devopswithprashant.service.blog;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.devopswithprashant.service.blog.api.dto.AuthorProfileResponse;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ProfileServiceConfiguration {

    @Bean
    RestClient profileServiceRestClient(
            RestClient.Builder builder,
            @Value("${profile.service.base-url}") String baseUrl,
            @Value("${profile.service.connect-timeout:2s}") Duration connectTimeout,
            @Value("${profile.service.read-timeout:3s}") Duration readTimeout) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);
        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    Cache<UUID, Optional<AuthorProfileResponse>> publicProfileCache(
            @Value("${profile.cache.maximum-size:1000}") long maximumSize,
            @Value("${profile.cache.ttl:10m}") Duration ttl) {
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(ttl)
                .<UUID, Optional<AuthorProfileResponse>>build();
    }
}
