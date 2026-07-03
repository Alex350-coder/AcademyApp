package com.academicsaas.academic.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.academicsaas.academic.domain.model.Course;
import com.academicsaas.academic.domain.model.CourseSection;
import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.model.Grade;
import com.academicsaas.academic.domain.model.valueobject.Score;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.domain.repository.CourseSectionRepository;
import com.academicsaas.academic.domain.repository.EnrollmentRepository;
import com.academicsaas.academic.domain.repository.GradeRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DetectAtRiskStudentsUseCase")
class DetectAtRiskStudentsUseCaseTest {

    private EnrollmentRepository enrollmentRepository;
    private GradeRepository gradeRepository;
    private CourseSectionRepository sectionRepository;
    private CourseRepository courseRepository;
    private DetectAtRiskStudentsUseCase useCase;
    private UUID institutionId;

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        gradeRepository = mock(GradeRepository.class);
        sectionRepository = mock(CourseSectionRepository.class);
        courseRepository = mock(CourseRepository.class);
        useCase = new DetectAtRiskStudentsUseCase(enrollmentRepository, gradeRepository, sectionRepository, courseRepository);
        institutionId = UUID.randomUUID();
    }

    private Enrollment createEnrollment(UUID studentId, UUID sectionId) {
        return Enrollment.create(UUID.randomUUID(), studentId, sectionId);
    }

    // Stubs the section/course lookup chain so a given sectionId resolves to
    // a course owned by this test's institutionId.
    private void stubSectionInOwnInstitution(UUID sectionId) {
        var courseId = UUID.randomUUID();
        var now = Instant.now();
        var section = new CourseSection(sectionId, courseId, UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), "Section", 30, 0, now, now);
        var course = new Course(courseId, "Course", "C-1", "desc", 3, institutionId, now, now);
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    }

    private Grade createGrade(UUID evaluationId, UUID studentId, double scoreValue, double maxScore, UUID gradedBy) {
        return Grade.create(
            UUID.randomUUID(),
            evaluationId,
            studentId,
            Score.of(scoreValue, maxScore),
            gradedBy
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should return empty list when no students are at risk")
        void should_returnEmpty_when_noAtRiskStudents() {
            var studentId = UUID.randomUUID();
            var sectionId = UUID.randomUUID();
            var enrollment = createEnrollment(studentId, sectionId);
            stubSectionInOwnInstitution(sectionId);

            when(enrollmentRepository.findAllActive()).thenReturn(List.of(enrollment));
            when(gradeRepository.findByStudentIdAndSectionId(studentId, sectionId))
                .thenReturn(List.of(
                    createGrade(UUID.randomUUID(), studentId, 90, 100, UUID.randomUUID()),
                    createGrade(UUID.randomUUID(), studentId, 85, 100, UUID.randomUUID())
                ));

            var request = new DetectAtRiskStudentsUseCase.Request(
                institutionId, UUID.randomUUID(), BigDecimal.valueOf(60), "academic");
            var result = useCase.execute(request);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return at-risk students when averages are below threshold")
        void should_returnAtRiskStudents_when_belowThreshold() {
            var studentId = UUID.randomUUID();
            var sectionId = UUID.randomUUID();
            var enrollment = createEnrollment(studentId, sectionId);
            stubSectionInOwnInstitution(sectionId);

            when(enrollmentRepository.findAllActive()).thenReturn(List.of(enrollment));
            when(gradeRepository.findByStudentIdAndSectionId(studentId, sectionId))
                .thenReturn(List.of(
                    createGrade(UUID.randomUUID(), studentId, 30, 100, UUID.randomUUID()),
                    createGrade(UUID.randomUUID(), studentId, 40, 100, UUID.randomUUID())
                ));

            var request = new DetectAtRiskStudentsUseCase.Request(
                institutionId, UUID.randomUUID(), BigDecimal.valueOf(60), "academic");
            var result = useCase.execute(request);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().studentId()).isEqualTo(studentId);
            assertThat(result.getFirst().currentAverage()).isEqualByComparingTo("35.00");
        }

        @Test
        @DisplayName("should process all active enrollments regardless of academicPeriodId")
        void should_includeAllEnrollments_when_noAcademicPeriodFilter() {
            var student1Id = UUID.randomUUID();
            var student2Id = UUID.randomUUID();
            var section1Id = UUID.randomUUID();
            var section2Id = UUID.randomUUID();

            var enrollment1 = createEnrollment(student1Id, section1Id);
            var enrollment2 = createEnrollment(student2Id, section2Id);
            stubSectionInOwnInstitution(section1Id);
            stubSectionInOwnInstitution(section2Id);

            when(enrollmentRepository.findAllActive()).thenReturn(List.of(enrollment1, enrollment2));
            when(gradeRepository.findByStudentIdAndSectionId(student1Id, section1Id))
                .thenReturn(List.of(
                    createGrade(UUID.randomUUID(), student1Id, 35, 100, UUID.randomUUID())
                ));
            when(gradeRepository.findByStudentIdAndSectionId(student2Id, section2Id))
                .thenReturn(List.of(
                    createGrade(UUID.randomUUID(), student2Id, 40, 100, UUID.randomUUID())
                ));

            var request = new DetectAtRiskStudentsUseCase.Request(
                institutionId, UUID.randomUUID(), BigDecimal.valueOf(50), "academic");
            var result = useCase.execute(request);

            assertThat(result).hasSize(2);
        }
    }
}
