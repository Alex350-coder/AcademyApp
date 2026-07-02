package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.EnrollmentJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEnrollmentRepository extends JpaRepository<EnrollmentJpaEntity, UUID> {

    List<EnrollmentJpaEntity> findBySectionId(UUID sectionId);

    List<EnrollmentJpaEntity> findByStudentIdAndStatus(UUID studentId, String status);

    List<EnrollmentJpaEntity> findByStudentId(UUID studentId);

    List<EnrollmentJpaEntity> findByStatus(String status);

    long countBySectionId(UUID sectionId);

    boolean existsByStudentIdAndSectionId(UUID studentId, UUID sectionId);
}
