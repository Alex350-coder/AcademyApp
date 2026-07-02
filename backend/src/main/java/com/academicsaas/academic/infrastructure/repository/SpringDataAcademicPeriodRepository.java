package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.AcademicPeriodJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataAcademicPeriodRepository extends JpaRepository<AcademicPeriodJpaEntity, UUID> {
    List<AcademicPeriodJpaEntity> findByInstitutionId(UUID institutionId);
}
