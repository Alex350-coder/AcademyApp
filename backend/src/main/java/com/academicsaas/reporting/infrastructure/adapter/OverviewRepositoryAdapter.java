package com.academicsaas.reporting.infrastructure.adapter;

import com.academicsaas.academic.infrastructure.repository.SpringDataAttendanceRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseSectionRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEnrollmentRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEvaluationRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataGradeRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataStudentRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataTeacherRepository;
import com.academicsaas.reporting.application.port.OverviewRepository;
import com.academicsaas.reporting.domain.model.InstitutionalOverview;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Repository;

@Repository
public class OverviewRepositoryAdapter implements OverviewRepository {

    private final SpringDataStudentRepository studentRepository;
    private final SpringDataTeacherRepository teacherRepository;
    private final SpringDataCourseSectionRepository sectionRepository;
    private final SpringDataGradeRepository gradeRepository;
    private final SpringDataEvaluationRepository evaluationRepository;
    private final SpringDataEnrollmentRepository enrollmentRepository;
    private final SpringDataAttendanceRepository attendanceRepository;

    public OverviewRepositoryAdapter(SpringDataStudentRepository studentRepository,
                                     SpringDataTeacherRepository teacherRepository,
                                     SpringDataCourseSectionRepository sectionRepository,
                                     SpringDataGradeRepository gradeRepository,
                                     SpringDataEvaluationRepository evaluationRepository,
                                     SpringDataEnrollmentRepository enrollmentRepository,
                                     SpringDataAttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.evaluationRepository = evaluationRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public InstitutionalOverview getInstitutionalOverview() {
        return new InstitutionalOverview(
            countStudents(), countTeachers(), countActiveSections(),
            calculateOverallAverage(), calculateOverallAttendanceRate());
    }

    @Override
    public long countStudents() {
        return studentRepository.count();
    }

    @Override
    public long countTeachers() {
        return teacherRepository.count();
    }

    @Override
    public long countActiveSections() {
        return sectionRepository.count();
    }

    @Override
    public BigDecimal calculateOverallAverage() {
        var grades = gradeRepository.findAll();
        if (grades.isEmpty()) return BigDecimal.ZERO;

        var total = BigDecimal.ZERO;
        for (var grade : grades) {
            var evaluation = evaluationRepository.findById(grade.getEvaluationId());
            if (evaluation.isPresent()) {
                var maxScore = evaluation.get().getMaxScore();
                if (maxScore.compareTo(BigDecimal.ZERO) > 0) {
                    var pct = grade.getScore().multiply(BigDecimal.valueOf(100))
                        .divide(maxScore, 2, RoundingMode.HALF_UP);
                    total = total.add(pct);
                }
            }
        }
        return total.divide(BigDecimal.valueOf(grades.size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateOverallAttendanceRate() {
        var allAttendance = attendanceRepository.findAll();
        if (allAttendance.isEmpty()) return BigDecimal.ZERO;

        var present = allAttendance.stream()
            .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
            .count();

        return BigDecimal.valueOf(present)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(allAttendance.size()), 2, RoundingMode.HALF_UP);
    }
}
