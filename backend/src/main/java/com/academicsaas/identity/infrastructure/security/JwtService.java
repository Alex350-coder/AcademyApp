package com.academicsaas.identity.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    public record AccessToken(String token, String userId, String email, List<String> roles, Instant expiresAt) {}

    public record RefreshToken(String token, String jti, Instant expiresAt) {}

    public AccessToken createAccessToken(String userId, String email, List<String> roles) {
        var now = Instant.now();
        var expiration = now.plusMillis(properties.getAccessTokenExpirationMs());
        var jti = UUID.randomUUID().toString();

        var token = Jwts.builder()
            .id(jti)
            .subject(userId)
            .issuer(properties.getIssuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .claim("email", email)
            .claim("roles", roles)
            .signWith(key)
            .compact();

        return new AccessToken(token, userId, email, roles, expiration);
    }

    public RefreshToken createRefreshToken() {
        var now = Instant.now();
        var expiration = now.plusMillis(properties.getRefreshTokenExpirationMs());
        var jti = UUID.randomUUID().toString();

        var token = Jwts.builder()
            .id(jti)
            .issuer(properties.getIssuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(key)
            .compact();

        return new RefreshToken(token, jti, expiration);
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException e) {
            throw new JwtValidationException("Invalid or expired token");
        }
    }

    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String message) {
            super(message);
        }
    }
}
