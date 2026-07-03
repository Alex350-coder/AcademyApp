package com.academicsaas.reporting.infrastructure.adapter;

import com.academicsaas.academic.infrastructure.entity.CourseSectionJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataAttendanceRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseSectionRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEnrollmentRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEvaluationRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataGradeRepository;
import com.academicsaas.reporting.application.port.CourseReportRepository;
import com.academicsaas.reporting.domain.model.CoursePerformanceData;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CourseReportRepositoryAdapter implements CourseReportRepository {

    private final SpringDataCourseRepository courseRepository;
    private final SpringDataCourseSectionRepository sectionRepository;
    private final SpringDataEnrollmentRepository enrollmentRepository;
    private final SpringDataEvaluationRepository evaluationRepository;
    private final SpringDataGradeRepository gradeRepository;
    private final SpringDataAttendanceRepository attendanceRepository;

    public CourseReportRepositoryAdapter(SpringDataCourseRepository courseRepository,
                                         SpringDataCourseSectionRepository sectionRepository,
                                         SpringDataEnrollmentRepository enrollmentRepository,
                                         SpringDataEvaluationRepository evaluationRepository,
                                         SpringDataGradeRepository gradeRepository,
                                         SpringDataAttendanceRepository attendanceRepository) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.evaluationRepository = evaluationRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public List<CoursePerformanceData> getCoursePerformance(UUID institutionId, UUID academicPeriodId) {
        var ownCourseIds = courseRepository.findByInstitutionId(institutionId).stream()
            .map(c -> c.getId())
            .collect(Collectors.toSet());

        var sections = academicPeriodId != null
            ? sectionRepository.findByAcademicPeriodId(academicPeriodId)
            : sectionRepository.findAll();

        return sections.stream()
            .filter(s -> ownCourseIds.contains(s.getCourseId()))
            .map(this::toCoursePerformance)
            .toList();
    }

    private CoursePerformanceData toCoursePerformance(CourseSectionJpaEntity section) {
        var course = courseRepository.findById(section.getCourseId());
        var courseName = course.map(c -> c.getName()).orElse("Unknown");
        var courseCode = course.map(c -> c.getCode()).orElse("N/A");

        var enrolledCount = (int) enrollmentRepository.countBySectionId(section.getId());

        var evaluations = evaluationRepository.findBySectionId(section.getId());
        var gradeSum = BigDecimal.ZERO;
        var gradeCount = 0;

        for (var eval : evaluations) {
            var grades = gradeRepository.findByEvaluationId(eval.getId());
            for (var grade : grades) {
                if (eval.getMaxScore().compareTo(BigDecimal.ZERO) > 0) {
                    var pct = grade.getScore().multiply(BigDecimal.valueOf(100))
                        .divide(eval.getMaxScore(), 2, RoundingMode.HALF_UP);
                    gradeSum = gradeSum.add(pct);
                    gradeCount++;
                }
            }
        }

        var avgScore = gradeCount > 0
            ? gradeSum.divide(BigDecimal.valueOf(gradeCount), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        var enrollmentIds = enrollmentRepository.findBySectionId(section.getId()).stream()
            .map(e -> e.getId())
            .toList();

        BigDecimal attendanceRate = BigDecimal.ZERO;
        if (!enrollmentIds.isEmpty()) {
            var allAttendance = attendanceRepository.findByEnrollmentIdIn(enrollmentIds);
            var total = allAttendance.size();
            if (total > 0) {
                var present = allAttendance.stream()
                    .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
                    .count();
                attendanceRate = BigDecimal.valueOf(present)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            }
        }

        return new CoursePerformanceData(
            section.getCourseId(),
            courseName,
            courseCode,
            avgScore,
            enrolledCount,
            attendanceRate
        );
    }
}
