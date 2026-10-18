package com.devopswithprashant.service.blog;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String BLOGS_PATH = "/api/blogs";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, BLOGS_PATH, BLOGS_PATH + "/**").permitAll()
                        .requestMatchers(BLOGS_PATH, BLOGS_PATH + "/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${JWT_SECRET:}") String jwtSecret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience) {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 48) {
            throw new IllegalStateException("JWT_SECRET must contain at least 48 UTF-8 bytes");
        }

        SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA384");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
        OAuth2TokenValidator<Jwt> requiredClaimsValidator = jwt -> {
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            if (expiresAt == null || !expiresAt.isAfter(now)) {
                return invalidToken("JWT exp claim is required and must be in the future");
            }
            Instant notBefore = jwt.getNotBefore();
            if (notBefore != null && notBefore.isAfter(now)) {
                return invalidToken("JWT nbf claim cannot be in the future");
            }
            if (jwt.getAudience() == null || !jwt.getAudience().contains(audience)) {
                return invalidToken("JWT audience is invalid");
            }
            String subject = jwt.getSubject();
            if (subject == null) {
                return invalidToken("JWT subject must be a UUID");
            }
            try {
                if (!UUID.fromString(subject).toString().equalsIgnoreCase(subject)) {
                    return invalidToken("JWT subject must be a UUID");
                }
            } catch (IllegalArgumentException exception) {
                return invalidToken("JWT subject must be a UUID");
            }
            Instant issuedAt = jwt.getIssuedAt();
            if (issuedAt != null && issuedAt.isAfter(now)) {
                return invalidToken("JWT iat claim cannot be in the future");
            }
            return OAuth2TokenValidatorResult.success();
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                requiredClaimsValidator));
        return decoder;
    }

    private static OAuth2TokenValidatorResult invalidToken(String description) {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", description, null));
    }
}
