package com.academicsaas.identity.infrastructure.persistence;

import com.academicsaas.identity.domain.model.PasswordResetToken;
import com.academicsaas.identity.domain.repository.PasswordResetTokenRepository;
import com.academicsaas.identity.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataPasswordResetTokenRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final SpringDataPasswordResetTokenRepository springRepository;

    public PasswordResetTokenRepositoryAdapter(SpringDataPasswordResetTokenRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        var saved = springRepository.save(toJpa(token));
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return springRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springRepository.deleteByUserId(userId);
    }

    private PasswordResetTokenJpaEntity toJpa(PasswordResetToken domain) {
        var jpa = new PasswordResetTokenJpaEntity();
        jpa.setId(domain.getId());
        jpa.setUserId(domain.getUserId());
        jpa.setToken(domain.getToken());
        jpa.setExpiresAt(domain.getExpiresAt());
        jpa.setUsed(domain.isUsed());
        jpa.setCreatedAt(domain.getCreatedAt());
        return jpa;
    }

    private PasswordResetToken toDomain(PasswordResetTokenJpaEntity jpa) {
        return new PasswordResetToken(
            jpa.getId(),
            jpa.getUserId(),
            jpa.getToken(),
            jpa.getExpiresAt(),
            jpa.isUsed(),
            jpa.getCreatedAt()
        );
    }
}
