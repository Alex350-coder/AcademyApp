package com.academicsaas.academic.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.academicsaas.academic.domain.model.CourseSection;
import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.repository.CourseSectionRepository;
import com.academicsaas.academic.domain.repository.EnrollmentRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollStudentUseCase Integration Test (mocked repos, real domain objects)")
class EnrollStudentUseCaseIntegrationTest {

    private EnrollmentRepository enrollmentRepository;
    private CourseSectionRepository courseSectionRepository;
    private EnrollStudentUseCase useCase;

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        courseSectionRepository = mock(CourseSectionRepository.class);
        useCase = new EnrollStudentUseCase(enrollmentRepository, courseSectionRepository);
    }

    @Test
    @DisplayName("should enroll student and increment enrolledCount")
    void should_enrollAndIncrementCount_when_happyPath() {
        var section = new CourseSection(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            null,
            "Math 101 - Section A",
            30,
            25,
            Instant.now(),
            Instant.now()
        );
        var studentId = UUID.randomUUID();
        var request = new EnrollStudentUseCase.Request(studentId, section.getId());

        when(courseSectionRepository.findById(section.getId())).thenReturn(Optional.of(section));
        when(enrollmentRepository.existsByStudentIdAndSectionId(studentId, section.getId())).thenReturn(false);
        when(enrollmentRepository.save(any())).thenAnswer(invocation -> {
            var saved = invocation.getArgument(0, Enrollment.class);
            return saved;
        });

        useCase.execute(request);

        assertThat(section.getEnrolledCount()).isEqualTo(26);
    }
}
