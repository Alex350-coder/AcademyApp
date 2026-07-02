package com.academicsaas.identity.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PasswordResetToken {
    private final UUID id;
    private final UUID userId;
    private final String token;
    private final Instant expiresAt;
    private boolean used;
    private final Instant createdAt;

    public PasswordResetToken(UUID id, UUID userId, String token, Instant expiresAt, boolean used, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.token = Objects.requireNonNull(token);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.used = used;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static PasswordResetToken create(UUID userId, String token, Instant expiresAt) {
        return new PasswordResetToken(UUID.randomUUID(), userId, token, expiresAt, false, Instant.now());
    }

    public boolean isValid() {
        return !used && Instant.now().isBefore(expiresAt);
    }

    public void markAsUsed() {
        this.used = true;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getToken() { return token; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public Instant getCreatedAt() { return createdAt; }
}
