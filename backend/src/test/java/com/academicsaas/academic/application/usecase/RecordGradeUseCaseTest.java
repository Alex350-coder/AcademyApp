package com.academicsaas.academic.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.academicsaas.academic.domain.exception.InvalidScoreException;
import com.academicsaas.academic.domain.model.Evaluation;
import com.academicsaas.academic.domain.model.Grade;
import com.academicsaas.academic.domain.model.valueobject.Score;
import com.academicsaas.academic.domain.repository.EvaluationRepository;
import com.academicsaas.academic.domain.repository.GradeRepository;
import com.academicsaas.shared.exception.NotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RecordGradeUseCase")
class RecordGradeUseCaseTest {

    private GradeRepository gradeRepository;
    private EvaluationRepository evaluationRepository;
    private RecordGradeUseCase useCase;

    @BeforeEach
    void setUp() {
        gradeRepository = mock(GradeRepository.class);
        evaluationRepository = mock(EvaluationRepository.class);
        useCase = new RecordGradeUseCase(gradeRepository, evaluationRepository);
    }

    private Evaluation createEvaluation(BigDecimal maxScore) {
        return new Evaluation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Midterm Exam",
            LocalDate.now(),
            maxScore,
            Instant.now(),
            Instant.now()
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should record grade successfully for valid evaluation and student")
        void should_recordGrade_when_validRequest() {
            var evaluation = createEvaluation(BigDecimal.valueOf(100));
            var studentId = UUID.randomUUID();
            var gradedBy = UUID.randomUUID();
            var request = new RecordGradeUseCase.Request(
                evaluation.getId(), studentId, BigDecimal.valueOf(85), gradedBy);

            when(evaluationRepository.findById(evaluation.getId())).thenReturn(Optional.of(evaluation));
            when(gradeRepository.findByEvaluationIdAndStudentId(evaluation.getId(), studentId))
                .thenReturn(Optional.empty());
            when(gradeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = useCase.execute(request);

            assertThat(response.gradeId()).isNotNull();
            verify(gradeRepository).save(any());
        }

        @Test
        @DisplayName("should throw InvalidScoreException when grade already exists")
        void should_throwInvalidScore_when_gradeExists() {
            var evaluation = createEvaluation(BigDecimal.valueOf(100));
            var studentId = UUID.randomUUID();
            var gradedBy = UUID.randomUUID();
            var request = new RecordGradeUseCase.Request(
                evaluation.getId(), studentId, BigDecimal.valueOf(85), gradedBy);
            var existingGrade = Grade.create(
                UUID.randomUUID(), evaluation.getId(), studentId,
                Score.of(BigDecimal.valueOf(85), BigDecimal.valueOf(100)), gradedBy);

            when(evaluationRepository.findById(evaluation.getId())).thenReturn(Optional.of(evaluation));
            when(gradeRepository.findByEvaluationIdAndStudentId(evaluation.getId(), studentId))
                .thenReturn(Optional.of(existingGrade));

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidScoreException.class)
                .hasMessageContaining("Grade already exists");

            verify(gradeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw NotFoundException when evaluation doesn't exist")
        void should_throwNotFound_when_evaluationMissing() {
            var evaluationId = UUID.randomUUID();
            var request = new RecordGradeUseCase.Request(
                evaluationId, UUID.randomUUID(), BigDecimal.valueOf(85), UUID.randomUUID());

            when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should throw exception when score exceeds maxScore")
        void should_throw_when_scoreExceedsMaxScore() {
            var evaluation = createEvaluation(BigDecimal.valueOf(100));
            var request = new RecordGradeUseCase.Request(
                evaluation.getId(), UUID.randomUUID(), BigDecimal.valueOf(150), UUID.randomUUID());

            when(evaluationRepository.findById(evaluation.getId())).thenReturn(Optional.of(evaluation));

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(com.academicsaas.shared.exception.ValidationException.class)
                .hasMessageContaining("exceed");
        }
    }
}
