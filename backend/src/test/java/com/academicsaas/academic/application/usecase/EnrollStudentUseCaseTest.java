package com.academicsaas.academic.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.academicsaas.academic.domain.exception.DuplicateEnrollmentException;
import com.academicsaas.academic.domain.exception.SectionCapacityExceededException;
import com.academicsaas.academic.domain.model.CourseSection;
import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.repository.CourseSectionRepository;
import com.academicsaas.academic.domain.repository.EnrollmentRepository;
import com.academicsaas.shared.exception.NotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EnrollStudentUseCase")
class EnrollStudentUseCaseTest {

    private EnrollmentRepository enrollmentRepository;
    private CourseSectionRepository courseSectionRepository;
    private EnrollStudentUseCase useCase;

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        courseSectionRepository = mock(CourseSectionRepository.class);
        useCase = new EnrollStudentUseCase(enrollmentRepository, courseSectionRepository);
    }

    private CourseSection createSectionWithCapacity(int capacity, int enrolledCount) {
        return new CourseSection(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            null,
            "Math 101 - Section A",
            capacity,
            enrolledCount,
            Instant.now(),
            Instant.now()
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should enroll student when section has capacity")
        void should_enrollStudent_when_sectionHasCapacity() {
            var sectionId = UUID.randomUUID();
            var studentId = UUID.randomUUID();
            var section = createSectionWithCapacity(30, 25);
            var request = new EnrollStudentUseCase.Request(studentId, sectionId);

            when(courseSectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
            when(enrollmentRepository.existsByStudentIdAndSectionId(studentId, sectionId)).thenReturn(false);
            when(enrollmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = useCase.execute(request);

            assertThat(response.enrollmentId()).isNotNull();
            verify(courseSectionRepository).save(section);
        }

        @Test
        @DisplayName("should throw NotFoundException when section doesn't exist")
        void should_throwNotFound_when_sectionMissing() {
            var sectionId = UUID.randomUUID();
            var studentId = UUID.randomUUID();
            var request = new EnrollStudentUseCase.Request(studentId, sectionId);

            when(courseSectionRepository.findById(sectionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw SectionCapacityExceededException when section is full")
        void should_throwCapacityExceeded_when_sectionFull() {
            var sectionId = UUID.randomUUID();
            var studentId = UUID.randomUUID();
            var section = createSectionWithCapacity(30, 30);
            var request = new EnrollStudentUseCase.Request(studentId, sectionId);

            when(courseSectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
            when(enrollmentRepository.existsByStudentIdAndSectionId(studentId, sectionId)).thenReturn(false);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(SectionCapacityExceededException.class)
                .hasMessageContaining("maximum capacity");
        }

        @Test
        @DisplayName("should throw DuplicateEnrollmentException when student already enrolled")
        void should_throwDuplicateEnrollment_when_alreadyEnrolled() {
            var sectionId = UUID.randomUUID();
            var studentId = UUID.randomUUID();
            var section = createSectionWithCapacity(30, 25);
            var request = new EnrollStudentUseCase.Request(studentId, sectionId);

            when(courseSectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
            when(enrollmentRepository.existsByStudentIdAndSectionId(studentId, sectionId)).thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DuplicateEnrollmentException.class)
                .hasMessageContaining("already enrolled");
        }
    }
}
