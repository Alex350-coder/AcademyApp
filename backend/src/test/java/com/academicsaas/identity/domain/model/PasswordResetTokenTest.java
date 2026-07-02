package com.academicsaas.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PasswordResetToken")
class PasswordResetTokenTest {

    @Nested
    @DisplayName("isValid")
    class IsValid {

        @Test
        @DisplayName("should return true when token has future expiry and is not used")
        void should_returnTrue_when_futureExpiryAndNotUsed() {
            var token = new PasswordResetToken(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token123",
                Instant.now().plusSeconds(3600),
                false,
                Instant.now()
            );

            assertThat(token.isValid()).isTrue();
        }

        @Test
        @DisplayName("should return false when token has past expiry")
        void should_returnFalse_when_pastExpiry() {
            var token = new PasswordResetToken(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token123",
                Instant.now().minusSeconds(1),
                false,
                Instant.now()
            );

            assertThat(token.isValid()).isFalse();
        }

        @Test
        @DisplayName("should return false when token expires at exactly the same time")
        void should_returnFalse_when_expiresAtExactNow() {
            var token = new PasswordResetToken(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token123",
                Instant.now(),
                false,
                Instant.now()
            );

            assertThat(token.isValid()).isFalse();
        }
    }

    @Nested
    @DisplayName("markAsUsed")
    class MarkAsUsed {

        @Test
        @DisplayName("should mark token as used and make isValid return false")
        void should_markUsed_when_markAsUsed() {
            var token = new PasswordResetToken(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token123",
                Instant.now().plusSeconds(3600),
                false,
                Instant.now()
            );

            token.markAsUsed();

            assertThat(token.isUsed()).isTrue();
            assertThat(token.isValid()).isFalse();
        }
    }
}
