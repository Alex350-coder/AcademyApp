package com.academicsaas.identity.infrastructure.persistence.repository;

import com.academicsaas.identity.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {

    Optional<PasswordResetTokenJpaEntity> findByToken(String token);

    void deleteByUserId(UUID userId);
}
