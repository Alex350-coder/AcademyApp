package com.academicsaas.academic.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.academicsaas.academic.domain.model.valueobject.EnrollmentStatus;
import com.academicsaas.shared.exception.ValidationException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Enrollment")
class EnrollmentTest {

    private final UUID studentId = UUID.randomUUID();
    private final UUID sectionId = UUID.randomUUID();

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should create active enrollment")
        void should_createActiveEnrollment_when_created() {
            var enrollment = Enrollment.create(UUID.randomUUID(), studentId, sectionId);

            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
            assertThat(enrollment.getStudentId()).isEqualTo(studentId);
            assertThat(enrollment.getSectionId()).isEqualTo(sectionId);
        }
    }

    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("should change status to WITHDRAWN")
        void should_setWithdrawn_when_withdrawActive() {
            var enrollment = Enrollment.create(UUID.randomUUID(), studentId, sectionId);

            enrollment.withdraw();

            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.WITHDRAWN);
        }

        @Test
        @DisplayName("should throw when withdrawing already withdrawn enrollment")
        void should_throw_when_alreadyWithdrawn() {
            var enrollment = Enrollment.create(UUID.randomUUID(), studentId, sectionId);
            enrollment.withdraw();

            assertThatThrownBy(() -> enrollment.withdraw())
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("withdrawn");
        }
    }

    @Nested
    @DisplayName("complete")
    class Complete {

        @Test
        @DisplayName("should change status to COMPLETED")
        void should_setCompleted_when_completeActive() {
            var enrollment = Enrollment.create(UUID.randomUUID(), studentId, sectionId);

            enrollment.complete();

            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        }

        @Test
        @DisplayName("should throw when completing already completed enrollment")
        void should_throw_when_alreadyCompleted() {
            var enrollment = Enrollment.create(UUID.randomUUID(), studentId, sectionId);
            enrollment.complete();

            assertThatThrownBy(() -> enrollment.complete())
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only active enrollments");
        }
    }
}
