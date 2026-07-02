package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.GradeJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataGradeRepository extends JpaRepository<GradeJpaEntity, UUID> {

    Optional<GradeJpaEntity> findByEvaluationIdAndStudentId(UUID evaluationId, UUID studentId);

    List<GradeJpaEntity> findByStudentId(UUID studentId);

    List<GradeJpaEntity> findByEvaluationId(UUID evaluationId);
}
