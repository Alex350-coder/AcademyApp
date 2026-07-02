package com.academicsaas.academic.domain.repository;

import com.academicsaas.academic.domain.model.Evaluation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRepository {
    Evaluation save(Evaluation evaluation);
    Optional<Evaluation> findById(UUID id);
    List<Evaluation> findBySectionId(UUID sectionId);
    List<Evaluation> findAll();
}
