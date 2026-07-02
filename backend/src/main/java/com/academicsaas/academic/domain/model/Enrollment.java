package com.academicsaas.academic.domain.model;

import com.academicsaas.academic.domain.model.valueobject.EnrollmentStatus;
import com.academicsaas.shared.exception.ValidationException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Enrollment {

    private final UUID id;
    private final UUID studentId;
    private final UUID sectionId;
    private EnrollmentStatus status;
    private final Instant enrolledAt;
    private Instant withdrawnAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public Enrollment(UUID id, UUID studentId, UUID sectionId, EnrollmentStatus status,
                      Instant enrolledAt, Instant withdrawnAt,
                      Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.studentId = Objects.requireNonNull(studentId);
        this.sectionId = Objects.requireNonNull(sectionId);
        this.status = Objects.requireNonNull(status);
        this.enrolledAt = Objects.requireNonNull(enrolledAt);
        this.withdrawnAt = withdrawnAt;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Enrollment create(UUID id, UUID studentId, UUID sectionId) {
        var now = Instant.now();
        return new Enrollment(id, studentId, sectionId, EnrollmentStatus.ACTIVE, now, null, now, now);
    }

    public void withdraw() {
        if (status != EnrollmentStatus.ACTIVE) {
            throw new ValidationException("Only active enrollments can be withdrawn");
        }
        this.status = EnrollmentStatus.WITHDRAWN;
        this.withdrawnAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void complete() {
        if (status != EnrollmentStatus.ACTIVE) {
            throw new ValidationException("Only active enrollments can be completed");
        }
        this.status = EnrollmentStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getStudentId() { return studentId; }
    public UUID getSectionId() { return sectionId; }
    public EnrollmentStatus getStatus() { return status; }
    public Instant getEnrolledAt() { return enrolledAt; }
    public Instant getWithdrawnAt() { return withdrawnAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
