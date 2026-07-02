package com.academicsaas.academic.infrastructure.repository;

import com.academicsaas.academic.infrastructure.entity.EvaluationTypeJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEvaluationTypeRepository extends JpaRepository<EvaluationTypeJpaEntity, UUID> {
}
