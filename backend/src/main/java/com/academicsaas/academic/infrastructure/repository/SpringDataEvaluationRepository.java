package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.EvaluationJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEvaluationRepository extends JpaRepository<EvaluationJpaEntity, UUID> {

    List<EvaluationJpaEntity> findBySectionId(UUID sectionId);
}
