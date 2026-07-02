package com.academicsaas.academic.application.usecase;

import com.academicsaas.academic.domain.exception.InvalidScoreException;
import com.academicsaas.academic.domain.model.Evaluation;
import com.academicsaas.academic.domain.model.Grade;
import com.academicsaas.academic.domain.model.valueobject.Score;
import com.academicsaas.academic.domain.repository.EvaluationRepository;
import com.academicsaas.academic.domain.repository.GradeRepository;
import com.academicsaas.shared.exception.NotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecordGradeUseCase {

    private final GradeRepository gradeRepository;
    private final EvaluationRepository evaluationRepository;

    public RecordGradeUseCase(GradeRepository gradeRepository, EvaluationRepository evaluationRepository) {
        this.gradeRepository = gradeRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public record Request(UUID evaluationId, UUID studentId, BigDecimal scoreValue, UUID gradedBy) {}

    public record Response(UUID gradeId) {}

    public Response execute(Request request) {
        var evaluation = findEvaluation(request.evaluationId());

        var score = Score.of(request.scoreValue(), evaluation.getMaxScore());

        var existing = gradeRepository.findByEvaluationIdAndStudentId(
            request.evaluationId(), request.studentId());
        if (existing.isPresent()) {
            throw new InvalidScoreException(
                "Grade already exists for this evaluation and student");
        }

        var grade = Grade.create(
            UUID.randomUUID(),
            request.evaluationId(),
            request.studentId(),
            score,
            request.gradedBy()
        );

        var saved = gradeRepository.save(grade);
        return new Response(saved.getId());
    }

    private Evaluation findEvaluation(UUID evaluationId) {
        return evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new NotFoundException("Evaluation", evaluationId));
    }
}
