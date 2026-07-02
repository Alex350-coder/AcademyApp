package com.academicsaas.academic.domain.repository;

import com.academicsaas.academic.domain.model.Grade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeRepository {

    Grade save(Grade grade);

    Optional<Grade> findById(UUID id);

    Optional<Grade> findByEvaluationIdAndStudentId(UUID evaluationId, UUID studentId);

    List<Grade> findByStudentIdAndSectionId(UUID studentId, UUID sectionId);
}
