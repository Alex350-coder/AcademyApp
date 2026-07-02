package com.academicsaas.identity.infrastructure.persistence.repository;

import com.academicsaas.identity.infrastructure.persistence.entity.InstitutionJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInstitutionRepository extends JpaRepository<InstitutionJpaEntity, UUID> {

    Optional<InstitutionJpaEntity> findByCode(String code);

    List<InstitutionJpaEntity> findByIsActiveTrue();

    boolean existsByCode(String code);
}
