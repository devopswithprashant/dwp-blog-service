package com.devopswithprashant.service.blog;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

class JwtDecoderTests {

    private static final String SECRET = "012345678901234567890123456789012345678901234567";
    private static final String ISSUER = "devopswithprashant-auth";
    private static final String AUDIENCE = "devopswithprashant-platform";
    private static final String SUBJECT = "96c8bbf1-22b1-49c6-8a0d-e31f9f9df31d";

    private JwtDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new SecurityConfig().jwtDecoder(SECRET, ISSUER, AUDIENCE);
    }

    @Test
    void acceptsValidAuthServiceJwt() throws JOSEException {
        assertThatCode(() -> decoder.decode(token(ISSUER, List.of(AUDIENCE), SUBJECT,
                Instant.now(), Instant.now().plusSeconds(300), Instant.now().minusSeconds(10))))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsIncorrectIssuer() throws JOSEException {
        assertInvalid(token("untrusted-issuer", List.of(AUDIENCE), SUBJECT,
                Instant.now(), Instant.now().plusSeconds(300), Instant.now().minusSeconds(10)));
    }

    @Test
    void rejectsIncorrectAudience() throws JOSEException {
        assertInvalid(token(ISSUER, List.of("another-audience"), SUBJECT,
                Instant.now(), Instant.now().plusSeconds(300), Instant.now().minusSeconds(10)));
    }

    @Test
    void rejectsNonUuidSubject() throws JOSEException {
        assertInvalid(token(ISSUER, List.of(AUDIENCE), "not-a-uuid",
                Instant.now(), Instant.now().plusSeconds(300), Instant.now().minusSeconds(10)));
    }

    @Test
    void rejectsMissingExpiry() throws JOSEException {
        assertInvalid(token(ISSUER, List.of(AUDIENCE), SUBJECT, Instant.now(), null,
                Instant.now().minusSeconds(10)));
    }

    @Test
    void rejectsExpiredToken() throws JOSEException {
        assertInvalid(token(ISSUER, List.of(AUDIENCE), SUBJECT,
                Instant.now().minusSeconds(300), Instant.now().minusSeconds(120), Instant.now().minusSeconds(600)));
    }

    @Test
    void rejectsFutureNotBeforeClaim() throws JOSEException {
        assertInvalid(token(ISSUER, List.of(AUDIENCE), SUBJECT,
                Instant.now(), Instant.now().plusSeconds(300), Instant.now().plusSeconds(30)));
    }

    @Test
    void rejectsFutureIssuedAt() throws JOSEException {
        assertInvalid(token(ISSUER, List.of(AUDIENCE), SUBJECT,
                Instant.now().plusSeconds(60), Instant.now().plusSeconds(300), Instant.now().minusSeconds(10)));
    }

    private void assertInvalid(String token) {
        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    private String token(String issuer, List<String> audience, String subject,
            Instant issuedAt, Instant expiresAt, Instant notBefore) throws JOSEException {
        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .audience(audience)
                .subject(subject)
                .notBeforeTime(Date.from(notBefore));
        if (issuedAt != null) {
            claims.issueTime(Date.from(issuedAt));
        }
        if (expiresAt != null) {
            claims.expirationTime(Date.from(expiresAt));
        }
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS384), claims.build());
        jwt.sign(new MACSigner(SECRET.getBytes(StandardCharsets.UTF_8)));
        return jwt.serialize();
    }
}
