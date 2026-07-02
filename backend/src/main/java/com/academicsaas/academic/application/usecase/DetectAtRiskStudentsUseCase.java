package com.academicsaas.academic.application.usecase;

import com.academicsaas.academic.domain.repository.EnrollmentRepository;
import com.academicsaas.academic.domain.repository.GradeRepository;
import com.academicsaas.academic.domain.service.AverageCalculator;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DetectAtRiskStudentsUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final AverageCalculator averageCalculator;

    public DetectAtRiskStudentsUseCase(
            EnrollmentRepository enrollmentRepository,
            GradeRepository gradeRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
        this.averageCalculator = new AverageCalculator();
    }

    public record AtRiskStudent(
        UUID studentId,
        BigDecimal currentAverage,
        String reason,
        UUID sectionId,
        String sectionName
    ) {}

    public record Request(UUID academicPeriodId, BigDecimal passingThreshold, String riskType) {}

    public List<AtRiskStudent> execute(Request request) {
        var threshold = request.passingThreshold() != null ? request.passingThreshold() : BigDecimal.valueOf(60);
        var riskType = request.riskType() != null ? request.riskType() : "all";

        var enrollments = enrollmentRepository.findAllActive();
        var atRisk = new java.util.ArrayList<AtRiskStudent>();

        for (var enrollment : enrollments) {
            var grades = gradeRepository.findByStudentIdAndSectionId(
                enrollment.getStudentId(), enrollment.getSectionId());

            var weightedGrades = grades.stream()
                .map(g -> {
                    var score = g.getScore();
                    return new AverageCalculator.WeightedGrade(
                        score.value(), score.maxScore(), BigDecimal.valueOf(100));
                })
                .toList();

            if (weightedGrades.isEmpty()) continue;

            var result = averageCalculator.calculate(weightedGrades);

            if ("all".equals(riskType) || "academic".equals(riskType)) {
                if (result.isAtRisk(threshold)) {
                    atRisk.add(new AtRiskStudent(
                        enrollment.getStudentId(),
                        result.average(),
                        "Bajo rendimiento acad\u00e9mico (promedio: " + result.average() + ")",
                        enrollment.getSectionId(),
                        "Section-" + enrollment.getSectionId().toString().substring(0, 8)
                    ));
                }
            }
        }

        return atRisk;
    }
}
