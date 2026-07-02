package com.academicsaas.identity.domain.event;

import com.academicsaas.identity.domain.model.User;
import java.time.Instant;

public record UserRegisteredEvent(
    String userId,
    String email,
    String fullName,
    Instant occurredAt
) {
    public static UserRegisteredEvent from(User user) {
        return new UserRegisteredEvent(
            user.getId().value().toString(),
            user.getEmail().value(),
            user.getFullName(),
            Instant.now()
        );
    }
}
