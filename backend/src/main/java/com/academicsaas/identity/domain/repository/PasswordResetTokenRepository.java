package com.academicsaas.identity.domain.repository;

import com.academicsaas.identity.domain.model.PasswordResetToken;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(UUID userId);
}
