package com.academicsaas.reporting.infrastructure.adapter;

import com.academicsaas.academic.infrastructure.repository.SpringDataAttendanceRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataCourseSectionRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEnrollmentRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataEvaluationRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataGradeRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataStudentRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataTeacherRepository;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.reporting.application.port.OverviewRepository;
import com.academicsaas.reporting.domain.model.InstitutionalOverview;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private final SpringDataCourseRepository courseRepository;
    private final SpringDataUserRepository userRepository;

    public OverviewRepositoryAdapter(SpringDataStudentRepository studentRepository,
                                     SpringDataTeacherRepository teacherRepository,
                                     SpringDataCourseSectionRepository sectionRepository,
                                     SpringDataGradeRepository gradeRepository,
                                     SpringDataEvaluationRepository evaluationRepository,
                                     SpringDataEnrollmentRepository enrollmentRepository,
                                     SpringDataAttendanceRepository attendanceRepository,
                                     SpringDataCourseRepository courseRepository,
                                     SpringDataUserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.evaluationRepository = evaluationRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public InstitutionalOverview getInstitutionalOverview(UUID institutionId) {
        return new InstitutionalOverview(
            countStudents(institutionId), countTeachers(institutionId), countActiveSections(institutionId),
            calculateOverallAverage(institutionId), calculateOverallAttendanceRate(institutionId));
    }

    @Override
    public long countStudents(UUID institutionId) {
        return studentRepository.findAll().stream()
            .filter(s -> belongsToInstitution(s.getUserId(), institutionId))
            .count();
    }

    @Override
    public long countTeachers(UUID institutionId) {
        return teacherRepository.findAll().stream()
            .filter(t -> belongsToInstitution(t.getUserId(), institutionId))
            .count();
    }

    @Override
    public long countActiveSections(UUID institutionId) {
        var ownSectionIds = ownSectionIds(institutionId);
        return sectionRepository.findAll().stream()
            .filter(s -> ownSectionIds.contains(s.getId()))
            .count();
    }

    @Override
    public BigDecimal calculateOverallAverage(UUID institutionId) {
        var ownSectionIds = ownSectionIds(institutionId);
        var ownEvaluationIds = ownSectionIds.stream()
            .flatMap(secId -> evaluationRepository.findBySectionId(secId).stream())
            .collect(Collectors.toMap(e -> e.getId(), e -> e.getMaxScore()));

        var total = BigDecimal.ZERO;
        var count = 0;
        for (var grade : gradeRepository.findAll()) {
            var maxScore = ownEvaluationIds.get(grade.getEvaluationId());
            if (maxScore != null && maxScore.compareTo(BigDecimal.ZERO) > 0) {
                var pct = grade.getScore().multiply(BigDecimal.valueOf(100))
                    .divide(maxScore, 2, RoundingMode.HALF_UP);
                total = total.add(pct);
                count++;
            }
        }
        if (count == 0) {
            return BigDecimal.ZERO;
        }
        return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateOverallAttendanceRate(UUID institutionId) {
        var ownSectionIds = ownSectionIds(institutionId);
        var ownEnrollmentIds = ownSectionIds.stream()
            .flatMap(secId -> enrollmentRepository.findBySectionId(secId).stream())
            .map(e -> e.getId())
            .collect(Collectors.toSet());

        var allAttendance = attendanceRepository.findAll().stream()
            .filter(a -> ownEnrollmentIds.contains(a.getEnrollmentId()))
            .toList();
        if (allAttendance.isEmpty()) {
            return BigDecimal.ZERO;
        }

        var present = allAttendance.stream()
            .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
            .count();

        return BigDecimal.valueOf(present)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(allAttendance.size()), 2, RoundingMode.HALF_UP);
    }

    private boolean belongsToInstitution(UUID userId, UUID institutionId) {
        return userRepository.findById(userId)
            .map(u -> institutionId.equals(u.getInstitutionId()))
            .orElse(false);
    }

    private Set<UUID> ownSectionIds(UUID institutionId) {
        var ownCourseIds = courseRepository.findByInstitutionId(institutionId).stream()
            .map(c -> c.getId())
            .collect(Collectors.toSet());
        return sectionRepository.findAll().stream()
            .filter(s -> ownCourseIds.contains(s.getCourseId()))
            .map(s -> s.getId())
            .collect(Collectors.toSet());
    }
}
