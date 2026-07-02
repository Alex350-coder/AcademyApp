package com.academicsaas.academic.application.usecase;

import com.academicsaas.academic.domain.repository.GradeRepository;
import com.academicsaas.academic.domain.service.AverageCalculator;
import com.academicsaas.academic.domain.service.AverageCalculator.AverageResult;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CalculateStudentAverageUseCase {

    private final GradeRepository gradeRepository;

    public CalculateStudentAverageUseCase(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public record Request(UUID studentId, UUID sectionId) {}

    public AverageResult execute(Request request) {
        var grades = gradeRepository.findByStudentIdAndSectionId(
            request.studentId(), request.sectionId());

        var calculator = new AverageCalculator();
        return calculator.calculate(
            grades.stream()
                .map(g -> {
                    var score = g.getScore();
                    return new AverageCalculator.WeightedGrade(
                        score.value(),
                        score.maxScore(),
                        BigDecimal.valueOf(100)
                    );
                })
                .toList()
        );
    }
}
